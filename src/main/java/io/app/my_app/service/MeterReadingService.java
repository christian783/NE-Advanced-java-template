package io.app.my_app.service;

import io.app.my_app.model.MeterReading;
import io.app.my_app.model.dtos.reading.MeterReadingFilter;
import io.app.my_app.model.dtos.reading.MeterReadingRequest;
import io.app.my_app.model.dtos.reading.MeterReadingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MeterReadingService {
    Page<MeterReadingResponse> findAll(MeterReadingFilter filter, Pageable pageable);

    MeterReadingResponse findById(UUID id);

    MeterReadingResponse capture(MeterReadingRequest request);

    MeterReading findReading(UUID id);
}
