package io.app.my_app.model.dtos.user;

import io.app.my_app.model.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusRequest {
    @NotNull(message = "{validation.user.status.required}")
    private UserStatus status;
}
