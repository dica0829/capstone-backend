package com.zoopick.server.dto.notification;


import com.zoopick.server.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendNotificationRequest {
    private String title;
    private String body;
    @NotBlank
    private NotificationType type;
    private Map<String, String> payload = Map.of();
}
