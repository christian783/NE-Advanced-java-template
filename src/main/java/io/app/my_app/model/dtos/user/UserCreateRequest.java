package io.app.my_app.model.dtos.user;

import io.app.my_app.model.enums.Role;
import io.app.my_app.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "{validation.auth.fullName.required}")
    private String fullName;

    @NotBlank(message = "{validation.auth.email.required}")
    @Email(message = "{validation.auth.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.auth.phone.required}")
    private String phoneNumber;

    @NotBlank(message = "{validation.auth.password.required}")
    @Size(min = 8, max = 100, message = "{validation.auth.password.size}")
    private String password;

    @NotNull(message = "{validation.user.role.required}")
    private Role role;

    @NotNull(message = "{validation.user.status.required}")
    private UserStatus status;
}
