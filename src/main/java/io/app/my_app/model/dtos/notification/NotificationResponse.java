package io.app.my_app.model.dtos.notification;

import io.app.my_app.model.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private UUID customerId;
    private UUID billId;
    private UUID paymentId;
    private NotificationType type;
    private String message;
    private Boolean sent;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
