package io.app.my_app.specification;

import io.app.my_app.model.User;
import io.app.my_app.model.dtos.user.UserFilter;
import io.app.my_app.model.enums.Role;
import io.app.my_app.model.enums.UserStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class UserSpec {

    public Specification<User> hasFilters(UserFilter filter) {
        if (filter == null) {
            filter = new UserFilter();
        }

        return Specification.where(hasId(filter.getId()))
                .and(hasEmail(filter.getEmail()))
                .and(hasFullName(filter.getFullName()))
                .and(hasRole(filter.getRole()))
                .and(hasStatus(filter.getStatus()));
    }

    private static Specification<User> hasId(UUID id) {
        return (root, query, cb) -> {
            if (id == null) return cb.conjunction();
            return cb.equal(root.get("id"), id);
        };
    }

    private static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<User> hasFullName(String fullName) {
        return (root, query, cb) -> {
            if (fullName == null || fullName.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase(Locale.ROOT) + "%");
        };
    }

    private static Specification<User> hasRole(Role role) {
        return (root, query, cb) -> {
            if (role == null) return cb.conjunction();
            return cb.equal(root.get("role"), role);
        };
    }

    private static Specification<User> hasStatus(UserStatus status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }
}

