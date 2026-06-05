package io.app.my_app.repository;

import io.app.my_app.model.Tariff;
import io.app.my_app.model.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface TariffRepository extends JpaSpecificationExecutor<Tariff>, JpaRepository<Tariff, UUID> {
    Optional<Tariff> findTopByMeterTypeAndActiveTrueOrderByTariffVersionDesc(MeterType meterType);

    Optional<Tariff> findTopByMeterTypeAndActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDescTariffVersionDesc(
            MeterType meterType,
            LocalDate effectiveFrom
    );
}
