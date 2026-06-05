package io.app.my_app.model.dtos.tariff;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffTierResponse {
    private UUID id;
    private BigDecimal minUnits;
    private BigDecimal maxUnits;
    private BigDecimal unitPrice;
}
