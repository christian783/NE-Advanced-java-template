package io.app.my_app.mapper;

import io.app.my_app.model.Notification;
import io.app.my_app.model.dtos.notification.NotificationResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "billId", source = "bill.id")
    @Mapping(target = "paymentId", source = "payment.id")
    NotificationResponse toResponse(Notification notification);
}
