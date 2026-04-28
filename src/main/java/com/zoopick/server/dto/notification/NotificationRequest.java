package com.zoopick.server.dto.notification;


import com.zoopick.server.entity.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class NotificationRequest {
    private String title;
    private String body;
    private NotificationType type;
    private Map<String, String> payload = Map.of();
}
