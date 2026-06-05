package io.app.my_app.model;

import io.app.my_app.audits.InitiatorAudit;
import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.TariffType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tariffs", indexes = {
        @Index(name = "idx_tariff_meter_type_effective_from", columnList = "meter_type,effective_from"),
        @Index(name = "idx_tariff_meter_type_version", columnList = "meter_type,tariff_version")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tariff extends InitiatorAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false, length = 30)
    private MeterType meterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false, length = 30)
    private TariffType tariffType;

    @Column(name = "unit_price", precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "fixed_charge", nullable = false, precision = 19, scale = 4)
    private BigDecimal fixedCharge;

    @Column(name = "vat_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal vatRate;

    @Column(name = "penalty_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal penaltyRate;

    @Column(name = "tariff_version", nullable = false)
    private Integer tariffVersion;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Builder.Default
    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TariffTier> tiers = new ArrayList<>();
}
