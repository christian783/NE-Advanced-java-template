package io.app.my_app.mapper;

import io.app.my_app.model.Customer;
import io.app.my_app.model.dtos.customer.CustomerRequest;
import io.app.my_app.model.dtos.customer.CustomerResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
builder = @Builder(disableBuilder = true))
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fullNames", source = "fullNames", qualifiedByName = "normalize")
    @Mapping(target = "nationalId", source = "nationalId", qualifiedByName = "normalize")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalize")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "normalize")
    @Mapping(target = "address", source = "address", qualifiedByName = "normalize")
    Customer toEntity(CustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletionStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "fullNames", source = "fullNames", qualifiedByName = "normalize")
    @Mapping(target = "nationalId", source = "nationalId", qualifiedByName = "normalize")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalize")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "normalize")
    @Mapping(target = "address", source = "address", qualifiedByName = "normalize")
    void updateEntity(CustomerRequest request, @MappingTarget Customer customer);

    @Named("normalize")
    default String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
