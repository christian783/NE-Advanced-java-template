package io.app.my_app.model.dtos.reading;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReadingRequest {
    @NotNull(message = "{validation.reading.meter.required}")
    private UUID meterId;

    @NotNull(message = "{validation.reading.previous.required}")
    @PositiveOrZero(message = "{validation.reading.previous.positive}")
    private BigDecimal previousReading;

    @NotNull(message = "{validation.reading.current.required}")
    @PositiveOrZero(message = "{validation.reading.current.positive}")
    private BigDecimal currentReading;

    @NotNull(message = "{validation.reading.date.required}")
    private LocalDate readingDate;
}
