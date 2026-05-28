package io.app.my_app.mapper;

import io.app.my_app.model.Product;
import io.app.my_app.model.dtos.product.ProductRequest;
import io.app.my_app.model.dtos.product.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletionStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "sku", source = "sku", qualifiedByName = "normalize")
    @Mapping(target = "name", source = "name", qualifiedByName = "normalize")
    @Mapping(target = "description", source = "description", qualifiedByName = "normalize")
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletionStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "sku", source = "sku", qualifiedByName = "normalize")
    @Mapping(target = "name", source = "name", qualifiedByName = "normalize")
    @Mapping(target = "description", source = "description", qualifiedByName = "normalize")
    void updateEntity(ProductRequest request, @MappingTarget Product product);

    @Named("normalize")
    default String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
