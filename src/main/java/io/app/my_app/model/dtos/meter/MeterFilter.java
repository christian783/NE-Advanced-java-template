package io.app.my_app.model.dtos.meter;

import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.UtilityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterFilter {
    private UUID id;
    private UUID customerId;
    private String meterNumber;
    private MeterType meterType;
    private UtilityStatus status;
}

