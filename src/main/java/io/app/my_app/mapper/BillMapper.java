package io.app.my_app.mapper;

import io.app.my_app.model.Bill;
import io.app.my_app.model.dtos.billing.BillResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
builder = @Builder(disableBuilder = true))
public interface BillMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.fullNames")
    @Mapping(target = "meterId", source = "meter.id")
    @Mapping(target = "meterNumber", source = "meter.meterNumber")
    @Mapping(target = "meterType", source = "meter.meterType")
    @Mapping(target = "approvedBy", source = "approvedBy.id")
    BillResponse toResponse(Bill bill);
}
