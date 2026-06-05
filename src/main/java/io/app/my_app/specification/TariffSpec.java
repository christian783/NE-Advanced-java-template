package io.app.my_app.specification;

import io.app.my_app.model.Tariff;
import io.app.my_app.model.dtos.tariff.TariffFilter;
import io.app.my_app.model.enums.MeterType;
import io.app.my_app.model.enums.TariffType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TariffSpec {

    public Specification<Tariff> hasFilters(TariffFilter filter) {
        if (filter == null) {
            filter = new TariffFilter();
        }

        return Specification.where(SoftDeleteSpec.<Tariff>notDeleted())
                .and(hasId(filter.getId()))
                .and(hasMeterType(filter.getMeterType()))
                .and(hasTariffType(filter.getTariffType()))
                .and(hasActive(filter.getActive()));
    }

    private static Specification<Tariff> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<Tariff> hasMeterType(MeterType meterType) {
        return (root, query, cb) -> {
            if (meterType == null) return cb.conjunction();
            return cb.equal(root.get("meterType"), meterType);
        };
    }

    private static Specification<Tariff> hasTariffType(TariffType tariffType) {
        return (root, query, cb) -> {
            if (tariffType == null) return cb.conjunction();
            return cb.equal(root.get("tariffType"), tariffType);
        };
    }

    private static Specification<Tariff> hasActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return cb.conjunction();
            return cb.equal(root.get("active"), active);
        };
    }
}

