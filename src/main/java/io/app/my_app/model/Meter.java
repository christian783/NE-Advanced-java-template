package io.app.my_app.model;

import io.app.my_app.audits.InitiatorAudit;
import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.UtilityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "meters", indexes = {
        @Index(name = "idx_meter_number", columnList = "meter_number"),
        @Index(name = "idx_meter_customer", columnList = "customer_id")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meter extends InitiatorAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "meter_number", nullable = false, unique = true, length = 100)
    private String meterNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false, length = 30)
    private MeterType meterType;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UtilityStatus status = UtilityStatus.ACTIVE;
}
