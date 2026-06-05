package io.app.my_app.model.dtos.auth;

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
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "{validation.auth.email.required}")
    @Email(message = "{validation.auth.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.auth.otp.required}")
    @Pattern(regexp = "\\d{4,10}", message = "{validation.auth.otp.invalid}")
    private String otp;

    @NotBlank(message = "{validation.auth.password.required}")
    @Size(min = 8, max = 100, message = "{validation.auth.password.size}")
    private String newPassword;

    @NotBlank(message = "{validation.auth.passwordConfirmation.required}")
    private String confirmationPassword;
}
