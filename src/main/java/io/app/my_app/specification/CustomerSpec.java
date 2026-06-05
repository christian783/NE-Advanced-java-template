package io.app.my_app.specification;

import io.app.my_app.model.Customer;
import io.app.my_app.model.dtos.customer.CustomerFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class CustomerSpec {

    public Specification<Customer> hasFilters(CustomerFilter filter) {
        if (filter == null) {
            filter = new CustomerFilter();
        }

        return Specification.where(SoftDeleteSpec.<Customer>notDeleted())
                .and(hasId(filter.getId()))
                .and(hasFullNames(filter.getFullNames()))
                .and(hasNationalId(filter.getNationalId()))
                .and(hasEmail(filter.getEmail()));
    }

    private static Specification<Customer> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<Customer> hasFullNames(String fullNames) {
        return (root, query, cb) -> {
            if (fullNames == null || fullNames.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("fullNames")), "%" + fullNames.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Customer> hasNationalId(String nationalId) {
        return (root, query, cb) -> {
            if (nationalId == null || nationalId.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("nationalId")), "%" + nationalId.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Customer> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase(Locale.ROOT) + "%");
        };
    }
}

