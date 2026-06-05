package io.app.my_app.service;

import io.app.my_app.model.Tariff;
import io.app.my_app.model.dtos.tariff.TariffFilter;
import io.app.my_app.model.dtos.tariff.TariffRequest;
import io.app.my_app.model.dtos.tariff.TariffResponse;
import io.app.my_app.model.enums.MeterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface TariffService {
    Page<TariffResponse> findAll(TariffFilter filter, Pageable pageable);

    TariffResponse findById(UUID id);

    TariffResponse configure(TariffRequest request);

    Tariff findApplicableTariff(MeterType meterType, LocalDate billingCycleStart);

    Tariff findTariff(UUID id);
}
