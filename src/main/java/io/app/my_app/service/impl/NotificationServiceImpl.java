package io.app.my_app.service.impl;

import io.app.my_app.model.Bill;
import io.app.my_app.model.Customer;
import io.app.my_app.model.Notification;
import io.app.my_app.model.Payment;
import io.app.my_app.mapper.NotificationMapper;
import io.app.my_app.model.dtos.notification.NotificationFilter;
import io.app.my_app.model.dtos.notification.NotificationResponse;
import io.app.my_app.model.enums.NotificationType;
import io.app.my_app.repository.NotificationRepository;
import io.app.my_app.service.NotificationService;
import io.app.my_app.specification.NotificationSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationSpec notificationSpec;

    @Override
    @Transactional
    public Notification createBillGeneratedNotification(Bill bill) {
        if (notificationRepository.existsByBillIdAndType(bill.getId(), NotificationType.BILL_GENERATED)) {
            return null;
        }
        return notificationRepository.save(Notification.builder()
                .customer(bill.getCustomer())
                .bill(bill)
                .type(NotificationType.BILL_GENERATED)
                .message(formatBillMessage(bill.getCustomer(), bill.getBillMonth(), bill.getBillYear(), bill.getTotalAmount()))
                .sent(false)
                .build());
    }

    @Override
    @Transactional
    public Notification createPaymentReceivedNotification(Payment payment) {
        Bill bill = payment.getBill();
        if (notificationRepository.existsByPaymentIdAndType(payment.getId(), NotificationType.PAYMENT_RECEIVED)) {
            return null;
        }
        return notificationRepository.save(Notification.builder()
                .customer(payment.getCustomer())
                .bill(bill)
                .payment(payment)
                .type(NotificationType.PAYMENT_RECEIVED)
                .message(formatBillMessage(payment.getCustomer(), bill.getBillMonth(), bill.getBillYear(), bill.getTotalAmount()))
                .sent(false)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> findAll(NotificationFilter filter, Pageable pageable) {
        return notificationRepository.findAll(notificationSpec.hasFilters(filter), pageable)
                .map(notificationMapper::toResponse);
    }

    private String formatBillMessage(Customer customer, Integer month, Integer year, BigDecimal amount) {
        String monthYear = Month.of(month).name().substring(0, 1)
                + Month.of(month).name().substring(1).toLowerCase(Locale.ROOT)
                + "/" + year;
        return "Dear " + customer.getFullNames() + ",\n"
                + "Your " + monthYear + " utility bill of " + amount.stripTrailingZeros().toPlainString()
                + " FRW has been successfully processed.";
    }

}
