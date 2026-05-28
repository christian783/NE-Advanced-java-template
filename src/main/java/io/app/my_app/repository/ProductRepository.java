package io.app.my_app.repository;

import io.app.my_app.model.Product;
import io.app.my_app.model.enums.DeletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaSpecificationExecutor<Product>, JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);

    Optional<Product> findByIdAndDeletionStatusAndDeletedAtIsNull(UUID id, DeletionStatus deletionStatus);
}
