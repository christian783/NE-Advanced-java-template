package io.app.my_app.mapper;

import io.app.my_app.model.Payment;
import io.app.my_app.model.dtos.payment.PaymentRequest;
import io.app.my_app.model.dtos.payment.PaymentResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
builder = @Builder(disableBuilder = true))
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bill", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "balanceBefore", ignore = true)
    @Mapping(target = "balanceAfter", ignore = true)
    @Mapping(target = "referenceNumber", source = "referenceNumber", qualifiedByName = "normalize")
    Payment toEntity(PaymentRequest request);

    @Mapping(target = "billReference", source = "bill.reference")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.fullNames")
    PaymentResponse toResponse(Payment payment);

    @Named("normalize")
    default String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
