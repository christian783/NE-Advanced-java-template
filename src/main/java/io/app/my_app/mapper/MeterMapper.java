package io.app.my_app.mapper;

import io.app.my_app.model.Meter;
import io.app.my_app.model.dtos.meter.MeterRequest;
import io.app.my_app.model.dtos.meter.MeterResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
builder = @Builder(disableBuilder = true))
public interface MeterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "meterNumber", source = "meterNumber", qualifiedByName = "normalize")
    Meter toEntity(MeterRequest request);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.fullNames")
    MeterResponse toResponse(Meter meter);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletionStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "meterNumber", source = "meterNumber", qualifiedByName = "normalize")
    void updateEntity(MeterRequest request, @MappingTarget Meter meter);

    @Named("normalize")
    default String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
