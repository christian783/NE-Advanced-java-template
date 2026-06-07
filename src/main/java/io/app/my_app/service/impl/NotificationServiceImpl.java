package io.app.my_app.service.impl;

import io.app.my_app.model.Bill;
import io.app.my_app.model.Customer;
import io.app.my_app.model.Notification;
import io.app.my_app.model.Payment;
import io.app.my_app.mapper.NotificationMapper;
import io.app.my_app.model.dtos.notification.NotificationFilter;
import io.app.my_app.model.dtos.notification.NotificationResponse;
import io.app.my_app.model.enums.NotificationType;
import io.app.my_app.repository.NotificationRepository;
import io.app.my_app.service.MailService;
import io.app.my_app.service.NotificationService;
import io.app.my_app.specification.NotificationSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationSpec notificationSpec;
    private final MailService mailService;

    @Override
    @Transactional
    public Notification createBillGeneratedNotification(Bill bill) {
        if (notificationRepository.existsByBillIdAndType(bill.getId(), NotificationType.BILL_GENERATED)) {
            return null;
        }

        Notification notification = notificationRepository.save(Notification.builder()
                .customer(bill.getCustomer())
                .bill(bill)
                .type(NotificationType.BILL_GENERATED)
                .message(formatBillMessage(bill.getCustomer(), bill.getBillMonth(), bill.getBillYear(), bill.getTotalAmount()))
                .sent(false)
                .build());

        // Send email notification within transaction
        sendBillNotificationEmail(bill);

        return notification;
    }

    @Override
    @Transactional
    public Notification createPaymentReceivedNotification(Payment payment) {
        Bill bill = payment.getBill();
        if (notificationRepository.existsByPaymentIdAndType(payment.getId(), NotificationType.PAYMENT_RECEIVED)) {
            return null;
        }

        Notification notification = notificationRepository.save(Notification.builder()
                .customer(payment.getCustomer())
                .bill(bill)
                .payment(payment)
                .type(NotificationType.PAYMENT_RECEIVED)
                .message(formatBillMessage(payment.getCustomer(), bill.getBillMonth(), bill.getBillYear(), bill.getTotalAmount()))
                .sent(false)
                .build());

        // Send email notification within transaction
        sendPaymentNotificationEmail(payment);

        return notification;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAll(NotificationFilter filter, Pageable pageable) {
        return notificationRepository.findAll(notificationSpec.hasFilters(filter), pageable)
                .map(notificationMapper::toResponse);
    }

    private String formatBillMessage(Customer customer, Integer month, Integer year, BigDecimal amount) {
        String monthYear = Month.of(month).name().substring(0, 1)
                + Month.of(month).name().substring(1).toLowerCase(Locale.ROOT)
                + "/" + year;
        return "Dear " + customer.getFullNames() + ",\n"
                + "Your " + monthYear + " utility bill of " + amount.stripTrailingZeros().toPlainString()
                + " FRW has been successfully processed.";
    }

    /**
     * Send bill generated notification email
     */
    private void sendBillNotificationEmail(Bill bill) {
        try {
            String customerEmail = bill.getCustomer().getEmail();
            if (customerEmail == null || customerEmail.isBlank()) {
                log.warn("Customer {} has no email address, skipping bill notification", bill.getCustomer().getId());
                return;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("customerName", bill.getCustomer().getFullNames());
            model.put("billMonth", formatMonthName(bill.getBillMonth()));
            model.put("billYear", bill.getBillYear());
            model.put("billReference", bill.getReference());
            model.put("consumption", bill.getConsumptionUnits().stripTrailingZeros().toPlainString());
            model.put("unitCharge", bill.getUnitCharge().stripTrailingZeros().toPlainString());
            model.put("fixedCharge", bill.getFixedCharge().stripTrailingZeros().toPlainString());
            model.put("vat", bill.getVatAmount().stripTrailingZeros().toPlainString());
            model.put("penalty", bill.getPenaltyAmount().stripTrailingZeros().toPlainString());
            model.put("totalAmount", bill.getTotalAmount().stripTrailingZeros().toPlainString());
            model.put("dueDate", formatDate(bill.getDueDate()));

            mailService.sendHtmlEmail(
                    customerEmail,
                    "Your Utility Bill for " + formatMonthName(bill.getBillMonth()) + " " + bill.getBillYear(),
                    "email/bill-notification",
                    model
            );

            log.info("Bill notification sent to {} for bill {}", customerEmail, bill.getId());
        } catch (Exception e) {
            log.error("Failed to send bill notification email for bill {}", bill.getId(), e);
            // Don't fail the main operation if email sending fails
        }
    }

    /**
     * Send payment received notification email
     */
    private void sendPaymentNotificationEmail(Payment payment) {
        try {
            String customerEmail = payment.getCustomer().getEmail();
            if (customerEmail == null || customerEmail.isBlank()) {
                log.warn("Customer {} has no email address, skipping payment notification", payment.getCustomer().getId());
                return;
            }

            Bill bill = payment.getBill();
            Map<String, Object> model = new HashMap<>();
            model.put("customerName", payment.getCustomer().getFullNames());
            model.put("amountPaid", payment.getAmountPaid().stripTrailingZeros().toPlainString());
            model.put("billReference", bill.getReference());
            model.put("billMonth", formatMonthName(bill.getBillMonth()));
            model.put("billYear", bill.getBillYear());
            model.put("paymentDate", formatDate(payment.getPaymentDate()));
            model.put("paymentMethod", payment.getPaymentMethod().name().replace("_", " "));
            model.put("referenceNumber", payment.getReferenceNumber() != null ? payment.getReferenceNumber() : "N/A");
            model.put("balanceBefore", payment.getBalanceBefore().stripTrailingZeros().toPlainString());
            model.put("balanceAfter", payment.getBalanceAfter().stripTrailingZeros().toPlainString());

            mailService.sendHtmlEmail(
                    customerEmail,
                    "Payment Received - Bill Reference: " + bill.getReference(),
                    "email/payment-notification",
                    model
            );

            log.info("Payment notification sent to {} for payment {}", customerEmail, payment.getId());
        } catch (Exception e) {
            log.error("Failed to send payment notification email for payment {}", payment.getId(), e);
            // Don't fail the main operation if email sending fails
        }
    }

    /**
     * Format month number to month name
     */
    private String formatMonthName(Integer month) {
        return Month.of(month).name().substring(0, 1)
                + Month.of(month).name().substring(1).toLowerCase(Locale.ROOT);
    }

    /**
     * Format LocalDate to readable format
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return date.format(formatter);
    }
}
