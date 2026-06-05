package io.app.my_app.repository;

import io.app.my_app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaSpecificationExecutor<Payment>, JpaRepository<Payment, UUID> {
    boolean existsByReferenceNumber(String referenceNumber);

    List<Payment> findByBillId(UUID billId);

    List<Payment> findByCustomerId(UUID customerId);
}
