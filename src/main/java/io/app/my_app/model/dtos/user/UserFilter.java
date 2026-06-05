package io.app.my_app.model.dtos.user;

import io.app.my_app.model.enums.Role;
import io.app.my_app.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter {
    private UUID id;
    private String email;
    private String fullName;
    private Role role;
    private UserStatus status;
}

