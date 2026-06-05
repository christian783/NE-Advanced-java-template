package io.app.my_app.repository;

import io.app.my_app.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends JpaSpecificationExecutor<Bill>, JpaRepository<Bill, UUID> {
    boolean existsByMeterIdAndBillMonthAndBillYear(UUID meterId, Integer billMonth, Integer billYear);

    Optional<Bill> findByReference(String reference);

    List<Bill> findByCustomerId(UUID customerId);
}
