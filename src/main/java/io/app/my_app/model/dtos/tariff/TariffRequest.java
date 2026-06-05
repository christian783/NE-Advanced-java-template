package io.app.my_app.model.dtos.tariff;

import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.TariffType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffRequest {
    @NotNull(message = "{validation.tariff.meterType.required}")
    private MeterType meterType;

    @NotNull(message = "{validation.tariff.type.required}")
    private TariffType tariffType;

    @PositiveOrZero(message = "{validation.tariff.unitPrice.positive}")
    private BigDecimal unitPrice;

    @NotNull(message = "{validation.tariff.fixedCharge.required}")
    @PositiveOrZero(message = "{validation.tariff.fixedCharge.positive}")
    private BigDecimal fixedCharge;

    @NotNull(message = "{validation.tariff.vatRate.required}")
    @PositiveOrZero(message = "{validation.tariff.vatRate.positive}")
    private BigDecimal vatRate;

    @NotNull(message = "{validation.tariff.penaltyRate.required}")
    @PositiveOrZero(message = "{validation.tariff.penaltyRate.positive}")
    private BigDecimal penaltyRate;

    @NotNull(message = "{validation.tariff.effectiveFrom.required}")
    private LocalDate effectiveFrom;

    @Builder.Default
    @Valid
    private List<TariffTierRequest> tiers = new ArrayList<>();
}
