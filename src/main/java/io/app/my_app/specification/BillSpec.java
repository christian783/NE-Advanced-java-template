package io.app.my_app.specification;

import io.app.my_app.model.Bill;
import io.app.my_app.model.dtos.billing.BillFilter;
import io.app.my_app.model.enums.BillStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class BillSpec {

    public Specification<Bill> hasFilters(BillFilter filter) {
        if (filter == null) {
            filter = new BillFilter();
        }

        return Specification.where(SoftDeleteSpec.<Bill>notDeleted())
                .and(hasId(filter.getId()))
                .and(hasCustomerId(filter.getCustomerId()))
                .and(hasMeterId(filter.getMeterId()))
                .and(hasReference(filter.getReference()))
                .and(hasStatus(filter.getStatus()))
                .and(hasBillMonth(filter.getBillMonth()))
                .and(hasBillYear(filter.getBillYear()));
    }

    private static Specification<Bill> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<Bill> hasCustomerId(UUID customerId) {
        return (root, query, cb) -> {
            if (customerId == null) return cb.conjunction();
            return cb.equal(root.get("customer").get("id"), customerId);
        };
    }

    private static Specification<Bill> hasMeterId(UUID meterId) {
        return (root, query, cb) -> {
            if (meterId == null) return cb.conjunction();
            return cb.equal(root.get("meter").get("id"), meterId);
        };
    }

    private static Specification<Bill> hasReference(String reference) {
        return (root, query, cb) -> {
            if (reference == null || reference.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("reference")), "%" + reference.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Bill> hasStatus(BillStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    private static Specification<Bill> hasBillMonth(Integer billMonth) {
        return (root, query, cb) -> {
            if (billMonth == null) return cb.conjunction();
            return cb.equal(root.get("billMonth"), billMonth);
        };
    }

    private static Specification<Bill> hasBillYear(Integer billYear) {
        return (root, query, cb) -> {
            if (billYear == null) return cb.conjunction();
            return cb.equal(root.get("billYear"), billYear);
        };
    }
}

