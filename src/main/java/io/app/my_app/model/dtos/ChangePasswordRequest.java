package io.app.my_app.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "{validation.auth.password.required}")
    private String currentPassword;

    @NotBlank(message = "{validation.auth.password.required}")
    @Size(min = 8, max = 100, message = "{validation.auth.password.size}")
    private String newPassword;

    @NotBlank(message = "{validation.auth.passwordConfirmation.required}")
    private String confirmationPassword;
}