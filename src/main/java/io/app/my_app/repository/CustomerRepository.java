package io.app.my_app.repository;

import io.app.my_app.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    boolean existsByNationalId(String nationalId);

    boolean existsByNationalIdAndIdNot(String nationalId, UUID id);

    Optional<Customer> findByNationalId(String nationalId);
}
