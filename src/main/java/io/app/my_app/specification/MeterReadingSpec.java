package io.app.my_app.specification;

import io.app.my_app.model.MeterReading;
import io.app.my_app.model.dtos.reading.MeterReadingFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MeterReadingSpec {

    public Specification<MeterReading> hasFilters(MeterReadingFilter filter) {
        if (filter == null) {
            filter = new MeterReadingFilter();
        }

        return Specification.where(SoftDeleteSpec.<MeterReading>notDeleted())
                .and(hasId(filter.getId()))
                .and(hasMeterId(filter.getMeterId()))
                .and(hasReadingMonth(filter.getReadingMonth()))
                .and(hasReadingYear(filter.getReadingYear()));
    }

    private static Specification<MeterReading> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<MeterReading> hasMeterId(UUID meterId) {
        return (root, query, cb) -> {
            if (meterId == null) return cb.conjunction();
            return cb.equal(root.get("meter").get("id"), meterId);
        };
    }

    private static Specification<MeterReading> hasReadingMonth(Integer readingMonth) {
        return (root, query, cb) -> {
            if (readingMonth == null) return cb.conjunction();
            return cb.equal(root.get("readingMonth"), readingMonth);
        };
    }

    private static Specification<MeterReading> hasReadingYear(Integer readingYear) {
        return (root, query, cb) -> {
            if (readingYear == null) return cb.conjunction();
            return cb.equal(root.get("readingYear"), readingYear);
        };
    }
}

