package io.app.my_app.model.dtos.user;

import io.app.my_app.model.enums.Role;
import io.app.my_app.model.enums.UserStatus;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private UserStatus status;
}
