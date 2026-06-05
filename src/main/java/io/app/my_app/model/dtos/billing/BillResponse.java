package io.app.my_app.model.dtos.billing;

import io.app.my_app.model.enums.BillStatus;
import io.app.my_app.model.enums.MeterType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private UUID id;
    private String reference;
    private UUID customerId;
    private String customerName;
    private UUID meterId;
    private String meterNumber;
    private MeterType meterType;
    private Integer billMonth;
    private Integer billYear;
    private BigDecimal consumptionUnits;
    private BigDecimal unitCharge;
    private BigDecimal fixedCharge;
    private BigDecimal vatAmount;
    private BigDecimal penaltyAmount;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal outstandingBalance;
    private BillStatus status;
    private LocalDate dueDate;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
}
