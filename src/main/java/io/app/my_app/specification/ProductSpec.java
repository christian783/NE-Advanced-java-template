package io.app.my_app.specification;

import io.app.my_app.model.Product;
import io.app.my_app.model.dtos.product.ProductFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class ProductSpec {

    public Specification<Product> hasFilters(ProductFilter productFilter) {
        if (productFilter == null) {
            productFilter = new ProductFilter();
        }

        return Specification.where(SoftDeleteSpec.<Product>notDeleted())
                .and(hasId(productFilter.getId()))
                .and(hasName(productFilter.getName()))
                .and(hasSku(productFilter.getSku()))
                .and(hasDescription(productFilter.getDescription()))
                .and(hasPrice(productFilter.getPrice()));
    }

    private static Specification<Product> hasId(UUID id) {
        return (root, query, criteriaBuilder) -> {
            if (id == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("id"), id);
        };
    }

    private static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Product> hasSku(String sku) {
        return (root, query, criteriaBuilder) -> {
            if (sku == null || sku.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")), "%" + sku.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Product> hasDescription(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null || description.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Product> hasPrice(Double price) {
        return (root, query, criteriaBuilder) -> {
            if (price == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("price"), price);
        };
    }
}
