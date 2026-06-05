package io.app.my_app.specification;

import io.app.my_app.model.Notification;
import io.app.my_app.model.dtos.notification.NotificationFilter;
import io.app.my_app.model.enums.NotificationType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationSpec {

    public Specification<Notification> hasFilters(NotificationFilter filter) {
        if (filter == null) {
            filter = new NotificationFilter();
        }

        return Specification.where(hasId(filter.getId()))
                .and(hasCustomerId(filter.getCustomerId()))
                .and(hasType(filter.getType()))
                .and(hasSent(filter.getSent()));
    }

    private static Specification<Notification> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<Notification> hasCustomerId(UUID customerId) {
        return (root, query, cb) -> {
            if (customerId == null) return cb.conjunction();
            return cb.equal(root.get("customer").get("id"), customerId);
        };
    }

    private static Specification<Notification> hasType(NotificationType type) {
        return (root, query, cb) -> {
            if (type == null) return cb.conjunction();
            return cb.equal(root.get("type"), type);
        };
    }

    private static Specification<Notification> hasSent(Boolean sent) {
        return (root, query, cb) -> {
            if (sent == null) return cb.conjunction();
            return cb.equal(root.get("sent"), sent);
        };
    }
}

