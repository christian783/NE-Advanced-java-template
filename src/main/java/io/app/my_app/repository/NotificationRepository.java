package io.app.my_app.repository;

import io.app.my_app.model.Notification;
import io.app.my_app.model.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaSpecificationExecutor<Notification>, JpaRepository<Notification, UUID> {
    List<Notification> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    boolean existsByBillIdAndType(UUID billId, NotificationType type);

    boolean existsByPaymentIdAndType(UUID paymentId, NotificationType type);
}
