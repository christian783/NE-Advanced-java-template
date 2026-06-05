package io.app.my_app.specification;

import io.app.my_app.model.Meter;
import io.app.my_app.model.dtos.meter.MeterFilter;
import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.UtilityStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class MeterSpec {

    public Specification<Meter> hasFilters(MeterFilter filter) {
        if (filter == null) {
            filter = new MeterFilter();
        }

        return Specification.where(SoftDeleteSpec.<Meter>notDeleted())
                .and(hasId(filter.getId()))
                .and(hasCustomerId(filter.getCustomerId()))
                .and(hasMeterNumber(filter.getMeterNumber()))
                .and(hasMeterType(filter.getMeterType()))
                .and(hasStatus(filter.getStatus()));
    }

    private static Specification<Meter> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<Meter> hasCustomerId(UUID customerId) {
        return (root, query, cb) -> {
            if (customerId == null) return cb.conjunction();
            return cb.equal(root.get("customer").get("id"), customerId);
        };
    }

    private static Specification<Meter> hasMeterNumber(String meterNumber) {
        return (root, query, cb) -> {
            if (meterNumber == null || meterNumber.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("meterNumber")), "%" + meterNumber.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<Meter> hasMeterType(MeterType meterType) {
        return (root, query, cb) -> {
            if (meterType == null) return cb.conjunction();
            return cb.equal(root.get("meterType"), meterType);
        };
    }

    private static Specification<Meter> hasStatus(UtilityStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }
}

