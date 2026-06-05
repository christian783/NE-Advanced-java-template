package io.app.my_app.model.dtos.billing;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class BillGenerateRequest {
    @NotNull(message = "{validation.bill.reading.required}")
    private UUID meterReadingId;

    @NotNull(message = "{validation.bill.dueDate.required}")
    private LocalDate dueDate;
}
