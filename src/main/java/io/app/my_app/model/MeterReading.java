package io.app.my_app.model;

import io.app.my_app.audits.InitiatorAudit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "meter_readings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_meter_reading_month_year", columnNames = {"meter_id", "reading_month", "reading_year"})
}, indexes = {
        @Index(name = "idx_meter_reading_meter", columnList = "meter_id"),
        @Index(name = "idx_meter_reading_period", columnList = "reading_year,reading_month")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading extends InitiatorAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private User operator;

    @Column(name = "previous_reading", nullable = false, precision = 19, scale = 4)
    private BigDecimal previousReading;

    @Column(name = "current_reading", nullable = false, precision = 19, scale = 4)
    private BigDecimal currentReading;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "reading_month", nullable = false)
    private Integer readingMonth;

    @Column(name = "reading_year", nullable = false)
    private Integer readingYear;

    @Column(name = "consumption", nullable = false, precision = 19, scale = 4)
    private BigDecimal consumption;
}
