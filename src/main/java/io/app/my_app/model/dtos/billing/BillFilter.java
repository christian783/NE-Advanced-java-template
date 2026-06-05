package io.app.my_app.model.dtos.billing;

import io.app.my_app.model.enums.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillFilter {
    private UUID id;
    private UUID customerId;
    private UUID meterId;
    private String reference;
    private BillStatus status;
    private Integer billMonth;
    private Integer billYear;
}

