package io.app.my_app.model.dtos.meter;

import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.UtilityStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterResponse {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private String meterNumber;
    private MeterType meterType;
    private LocalDate installationDate;
    private UtilityStatus status;
}
