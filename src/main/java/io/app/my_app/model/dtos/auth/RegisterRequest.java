package io.app.my_app.model.dtos.auth;

import io.app.my_app.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "{validation.auth.fullName.required}")
    private String fullName;

    @NotBlank(message = "{validation.auth.email.required}")
    @Email(message = "{validation.auth.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.auth.phone.required}")
    @Pattern(regexp = "^\\+2507\\d{8}$", message = "invalid phone number format. Expected format: +2507XXXXXXXX")
    private String phoneNumber;

    @NotBlank(message = "{validation.auth.password.required}")
    @Size(min = 8, max = 100, message = "{validation.auth.password.size}")
    private String password;
}
