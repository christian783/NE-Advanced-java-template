package io.app.my_app.model.dtos.tariff;

import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.TariffType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffFilter {
    private UUID id;
    private MeterType meterType;
    private TariffType tariffType;
    private Boolean active;
}

