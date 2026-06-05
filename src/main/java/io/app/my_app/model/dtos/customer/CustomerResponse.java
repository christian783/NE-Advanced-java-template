package io.app.my_app.model.dtos.customer;

import io.app.my_app.model.enums.UtilityStatus;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String fullNames;
    private String nationalId;
    private String email;
    private String phoneNumber;
    private String address;
    private UtilityStatus status;
}
