-- PostgreSQL database routines for Utility Billing System notifications.
-- Run this after Hibernate has created/updated the tables.
-- The Java services also create notifications so the API works even before
-- these routines are installed. These routines satisfy the database-level
-- trigger requirement for DBMS-managed environments.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION utility_format_bill_message(
    customer_name text,
    bill_month integer,
    bill_year integer,
    amount numeric
) RETURNS text AS $$
BEGIN
    RETURN 'Dear ' || customer_name || ',' || chr(10) ||
           'Your ' || to_char(make_date(bill_year, bill_month, 1), 'FMMonth/YYYY') ||
           ' utility bill of ' || trim(to_char(amount, 'FM999999999999990.00')) ||
           ' FRW has been successfully processed.';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION trg_notify_bill_generated()
RETURNS trigger AS $$
DECLARE
    customer_name text;
BEGIN
    SELECT full_names INTO customer_name
    FROM customers
    WHERE id = NEW.customer_id;

    IF EXISTS (
        SELECT 1 FROM notifications
        WHERE bill_id = NEW.id AND type = 'BILL_GENERATED'
    ) THEN
        RETURN NEW;
    END IF;

    INSERT INTO notifications (
        id,
        customer_id,
        bill_id,
        type,
        message,
        sent,
        created_at
    )
    VALUES (
        gen_random_uuid(),
        NEW.customer_id,
        NEW.id,
        'BILL_GENERATED',
        utility_format_bill_message(customer_name, NEW.bill_month, NEW.bill_year, NEW.total_amount),
        false,
        now()
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS bill_generated_notification_trigger ON bills;
CREATE TRIGGER bill_generated_notification_trigger
AFTER INSERT ON bills
FOR EACH ROW
EXECUTE FUNCTION trg_notify_bill_generated();

CREATE OR REPLACE FUNCTION trg_notify_full_payment()
RETURNS trigger AS $$
DECLARE
    customer_name text;
    target_bill bills%ROWTYPE;
BEGIN
    SELECT * INTO target_bill
    FROM bills
    WHERE id = NEW.bill_id;

    IF target_bill.outstanding_balance = 0 AND target_bill.status <> 'PAID' THEN
        UPDATE bills
        SET status = 'PAID'
        WHERE id = NEW.bill_id;
    END IF;

    IF target_bill.outstanding_balance = 0 AND NOT EXISTS (
        SELECT 1 FROM notifications
        WHERE payment_id = NEW.id AND type = 'PAYMENT_RECEIVED'
    ) THEN
        SELECT full_names INTO customer_name
        FROM customers
        WHERE id = NEW.customer_id;

        INSERT INTO notifications (
            id,
            customer_id,
            bill_id,
            payment_id,
            type,
            message,
            sent,
            created_at
        )
        VALUES (
            gen_random_uuid(),
            NEW.customer_id,
            NEW.bill_id,
            NEW.id,
            'PAYMENT_RECEIVED',
            utility_format_bill_message(customer_name, target_bill.bill_month, target_bill.bill_year, target_bill.total_amount),
            false,
            now()
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS full_payment_notification_trigger ON payments;
CREATE TRIGGER full_payment_notification_trigger
AFTER INSERT ON payments
FOR EACH ROW
EXECUTE FUNCTION trg_notify_full_payment();
