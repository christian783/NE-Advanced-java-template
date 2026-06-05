package io.app.my_app.model.dtos.payment;

import io.app.my_app.model.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private String billReference;
    private UUID customerId;
    private String customerName;
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
    private String referenceNumber;
    private LocalDate paymentDate;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
}
