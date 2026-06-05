package io.app.my_app.model.dtos.meter;

import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.UtilityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterRequest {
    @NotNull(message = "{validation.meter.customer.required}")
    private UUID customerId;

    @NotBlank(message = "{validation.meter.number.required}")
    private String meterNumber;

    @NotNull(message = "{validation.meter.type.required}")
    private MeterType meterType;

    @NotNull(message = "{validation.meter.installationDate.required}")
    private LocalDate installationDate;

    @NotNull(message = "{validation.meter.status.required}")
    private UtilityStatus status;
}
