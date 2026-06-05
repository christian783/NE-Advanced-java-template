package io.app.my_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tariff_tiers")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TariffTier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "min_units", nullable = false, precision = 19, scale = 4)
    private BigDecimal minUnits;

    @Column(name = "max_units", precision = 19, scale = 4)
    private BigDecimal maxUnits;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;
}
