package io.app.my_app.model;

import io.app.my_app.audits.InitiatorAudit;
import io.app.my_app.model.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bills", uniqueConstraints = {
        @UniqueConstraint(name = "uk_bill_reference", columnNames = "reference"),
        @UniqueConstraint(name = "uk_bill_meter_month_year", columnNames = {"meter_id", "bill_month", "bill_year"})
}, indexes = {
        @Index(name = "idx_bill_customer", columnList = "customer_id"),
        @Index(name = "idx_bill_status", columnList = "status")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bill extends InitiatorAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "reference", nullable = false, unique = true, length = 80)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_reading_id", nullable = false, unique = true)
    private MeterReading meterReading;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "bill_month", nullable = false)
    private Integer billMonth;

    @Column(name = "bill_year", nullable = false)
    private Integer billYear;

    @Column(name = "consumption_units", nullable = false, precision = 19, scale = 4)
    private BigDecimal consumptionUnits;

    @Column(name = "unit_charge", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitCharge;

    @Column(name = "fixed_charge", nullable = false, precision = 19, scale = 4)
    private BigDecimal fixedCharge;

    @Column(name = "vat_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal vatAmount;

    @Column(name = "penalty_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyAmount;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountPaid;

    @Column(name = "outstanding_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal outstandingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BillStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
}
