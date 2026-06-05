package io.app.my_app.repository;

import io.app.my_app.model.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeterReadingRepository extends JpaSpecificationExecutor<MeterReading>, JpaRepository<MeterReading, UUID> {
    boolean existsByMeterIdAndReadingMonthAndReadingYear(UUID meterId, Integer readingMonth, Integer readingYear);

    Optional<MeterReading> findTopByMeterIdOrderByReadingYearDescReadingMonthDesc(UUID meterId);

    List<MeterReading> findByMeterId(UUID meterId);
}
