package io.app.my_app.service;

import io.app.my_app.model.dtos.payment.PaymentFilter;
import io.app.my_app.model.dtos.payment.PaymentRequest;
import io.app.my_app.model.dtos.payment.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    Page<PaymentResponse> findAll(PaymentFilter filter, Pageable pageable);

    PaymentResponse record(PaymentRequest request);
}
