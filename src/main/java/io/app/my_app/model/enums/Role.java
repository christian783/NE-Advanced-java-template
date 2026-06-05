package io.app.my_app.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.app.my_app.model.enums.Permission.*;

@RequiredArgsConstructor
public enum Role {

    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE
            )
    ),
    OPERATOR(
            Set.of(
                    OPERATOR_READ,
                    OPERATOR_UPDATE,
                    OPERATOR_CREATE,
                    OPERATOR_DELETE
            )
    ),
    FINANCE(
            Set.of(
                    FINANCE_CREATE,
                    FINANCE_READ,
                    FINANCE_DELETE,
                    FINANCE_UPDATE
            )
    ),
    CUSTOMER(
            Set.of(
                   CUSTOMER_CREATE,
                   CUSTOMER_READ,
                   CUSTOMER_UPDATE,
                   CUSTOMER_DELETE
            )
    )

    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
