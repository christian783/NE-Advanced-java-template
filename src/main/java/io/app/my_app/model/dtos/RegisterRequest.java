package io.app.my_app.model.dtos;

import io.app.my_app.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "{validation.auth.firstname.required}")
    private String firstname;

    @NotBlank(message = "{validation.auth.lastname.required}")
    private String lastname;

    @NotBlank(message = "{validation.auth.email.required}")
    @Email(message = "{validation.auth.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.auth.password.required}")
    @Size(min = 8, max = 100, message = "{validation.auth.password.size}")
    private String password;

    @NotNull(message = "{validation.auth.role.required}")
    private Role role;
}