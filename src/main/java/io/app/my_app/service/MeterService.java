package io.app.my_app.service;

import io.app.my_app.model.Meter;
import io.app.my_app.model.dtos.meter.MeterFilter;
import io.app.my_app.model.dtos.meter.MeterRequest;
import io.app.my_app.model.dtos.meter.MeterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MeterService {
    Page<MeterResponse> findAll(MeterFilter filter, Pageable pageable);

    MeterResponse findById(UUID id);

    MeterResponse create(MeterRequest request);

    MeterResponse update(UUID id, MeterRequest request);

    Meter findMeter(UUID id);

    Meter findActiveMeter(UUID id);
}
