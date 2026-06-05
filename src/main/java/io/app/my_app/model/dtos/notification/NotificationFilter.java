package io.app.my_app.model.dtos.notification;

import io.app.my_app.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFilter {
    private UUID id;
    private UUID customerId;
    private NotificationType type;
    private Boolean sent;
}

