package io.app.my_app.specification;

import io.app.my_app.audits.InitiatorAudit;
import io.app.my_app.model.enums.DeletionStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class SoftDeleteSpec {
    public static <T extends InitiatorAudit>Specification<T> notDeleted() {
        return (root, query, criteriaBuilder) -> {
            Predicate deletedAtIsNull = criteriaBuilder.isNull(root.get("deletedAt"));
            Predicate DeletionStatusIsActive = criteriaBuilder.equal(root.get("deletionStatus"), DeletionStatus.ACTIVE);
            return criteriaBuilder.and(deletedAtIsNull, DeletionStatusIsActive);
        };
    }
}
