package io.app.my_app.repository;

import io.app.my_app.model.Meter;
import io.app.my_app.model.enums.UtilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeterRepository extends JpaSpecificationExecutor<Meter>, JpaRepository<Meter, UUID> {
    boolean existsByMeterNumber(String meterNumber);

    boolean existsByMeterNumberAndIdNot(String meterNumber, UUID id);

    List<Meter> findByCustomerId(UUID customerId);

    Optional<Meter> findByIdAndStatus(UUID id, UtilityStatus status);
}
