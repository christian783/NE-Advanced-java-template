package io.app.my_app.model.dtos.reading;

import io.app.my_app.model.enums.MeterType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReadingResponse {
    private UUID id;
    private UUID meterId;
    private String meterNumber;
    private MeterType meterType;
    private BigDecimal previousReading;
    private BigDecimal currentReading;
    private BigDecimal consumption;
    private LocalDate readingDate;
    private Integer readingMonth;
    private Integer readingYear;
}
