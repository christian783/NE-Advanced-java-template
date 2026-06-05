package io.app.my_app.specification;

import io.app.my_app.model.Payment;
import io.app.my_app.model.dtos.payment.PaymentFilter;
import io.app.my_app.model.enums.PaymentMethod;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class PaymentSpec {

    public Specification<Payment> hasFilters(PaymentFilter filter) {
        if (filter == null) {
            filter = new PaymentFilter();
        }

        return Specification.where(SoftDeleteSpec.<Payment>notDeleted())
                .and(hasId(filter.getId()))
                .and(hasCustomerId(filter.getCustomerId()))
                .and(hasBillId(filter.getBillId()))
                .and(hasReferenceNumber(filter.getReferenceNumber()))
                .and(hasMethod(filter.getMethod()));
    }

    private static Specification<Payment> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<Payment> hasCustomerId(UUID customerId) {
        return (root, query, cb) -> {
            if (customerId == null) return cb.conjunction();
            return cb.equal(root.get("customer").get("id"), customerId);
        };
    }

    private static Specification<Payment> hasBillId(UUID billId) {
        return (root, query, cb) -> {
            if (billId == null) return cb.conjunction();
            return cb.equal(root.get("bill").get("id"), billId);
        };
    }

    private static Specification<Payment> hasReferenceNumber(String referenceNumber) {
        return (root, query, cb) -> {
            if (referenceNumber == null || referenceNumber.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("referenceNumber")), "%" + referenceNumber.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Payment> hasMethod(PaymentMethod method) {
        return (root, query, cb) -> {
            if (method == null) return cb.conjunction();
            return cb.equal(root.get("method"), method);
        };
    }
}

