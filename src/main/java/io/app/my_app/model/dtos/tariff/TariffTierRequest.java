package io.app.my_app.model.dtos.tariff;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffTierRequest {
    @NotNull(message = "{validation.tariff.tier.min.required}")
    @PositiveOrZero(message = "{validation.tariff.tier.min.positive}")
    private BigDecimal minUnits;

    private BigDecimal maxUnits;

    @NotNull(message = "{validation.tariff.tier.unitPrice.required}")
    @PositiveOrZero(message = "{validation.tariff.unitPrice.positive}")
    private BigDecimal unitPrice;
}
