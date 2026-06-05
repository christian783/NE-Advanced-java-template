package io.app.my_app.model.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter {
    private UUID id;
    private String fullNames;
    private String nationalId;
    private String email;
}

