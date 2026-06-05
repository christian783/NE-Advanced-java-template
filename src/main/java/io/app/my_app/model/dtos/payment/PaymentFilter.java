package io.app.my_app.model.dtos.payment;

import io.app.my_app.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFilter {
    private UUID id;
    private UUID customerId;
    private UUID billId;
    private String referenceNumber;
    private PaymentMethod method;
}

