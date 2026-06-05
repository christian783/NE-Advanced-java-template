package io.app.my_app.model.dtos.tariff;

import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.TariffType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffResponse {
    private UUID id;
    private MeterType meterType;
    private TariffType tariffType;
    private BigDecimal unitPrice;
    private BigDecimal fixedCharge;
    private BigDecimal vatRate;
    private BigDecimal penaltyRate;
    private Integer tariffVersion;
    private LocalDate effectiveFrom;
    private Boolean active;
    private List<TariffTierResponse> tiers;
}
