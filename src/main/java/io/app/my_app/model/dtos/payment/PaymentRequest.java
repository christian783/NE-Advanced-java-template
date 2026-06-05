package io.app.my_app.model.dtos.payment;

import io.app.my_app.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "{validation.payment.billReference.required}")
    private String billReference;

    @NotNull(message = "{validation.payment.amount.required}")
    @Positive(message = "{validation.payment.amount.positive}")
    private BigDecimal amountPaid;

    @NotNull(message = "{validation.payment.method.required}")
    private PaymentMethod paymentMethod;

    private String referenceNumber;

    @NotNull(message = "{validation.payment.date.required}")
    private LocalDate paymentDate;
}
