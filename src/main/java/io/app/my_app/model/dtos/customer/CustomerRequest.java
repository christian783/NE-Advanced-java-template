package io.app.my_app.model.dtos.customer;

import io.app.my_app.model.enums.UtilityStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    @NotBlank(message = "{validation.customer.fullNames.required}")
    private String fullNames;

    @NotBlank(message = "{validation.customer.nationalId.required}")
    @Digits(integer = 16, fraction = 0, message = "Invalid national id number format. It should contain exactly 16 digits.")
    private String nationalId;

    @Email(message = "{validation.customer.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.customer.phone.required}")
    private String phoneNumber;

    @NotBlank(message = "{validation.customer.address.required}")
    private String address;

    @NotNull(message = "{validation.customer.status.required}")
    private UtilityStatus status;
}
