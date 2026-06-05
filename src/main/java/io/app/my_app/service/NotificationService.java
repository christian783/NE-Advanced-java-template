package io.app.my_app.service;

import io.app.my_app.model.Bill;
import io.app.my_app.model.Notification;
import io.app.my_app.model.Payment;
import io.app.my_app.model.dtos.notification.NotificationFilter;
import io.app.my_app.model.dtos.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    Notification createBillGeneratedNotification(Bill bill);

    Notification createPaymentReceivedNotification(Payment payment);

    Page<NotificationResponse> findAll(NotificationFilter filter, Pageable pageable);
}
