package io.app.my_app.model.dtos.reading;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterReadingFilter {
    private UUID id;
    private UUID meterId;
    private Integer readingMonth;
    private Integer readingYear;
}

