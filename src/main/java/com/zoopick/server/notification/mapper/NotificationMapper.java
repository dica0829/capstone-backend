package com.zoopick.server.notification.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoopick.server.auth.entity.User;
import com.zoopick.server.notification.SendNotificationCommand;
import com.zoopick.server.notification.dto.NotificationRecord;
import com.zoopick.server.notification.entity.ZoopickNotification;
import com.zoopick.server.notification.payload.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final ObjectMapper objectMapper;
    private final NotificationPayloadMapper notificationPayloadMapper;

    public NotificationRecord toNotificationResponse(ZoopickNotification notification) {
        NotificationRecord notificationRecord = new NotificationRecord();
        notificationRecord.setId(notification.getId());
        notificationRecord.setType(notification.getType());
        notificationRecord.setCreatedAt(notification.getCreatedAt());
        NotificationPayload payload = notificationPayloadMapper.toNotificationPayload(notification.getPayload(), notification.getType());
        notificationRecord.setPayload(payload);
        notificationRecord.setReadAt(notification.getReadAt());

        return notificationRecord;
    }

    public ZoopickNotification toZoopickNotification(User user, SendNotificationCommand command) {
        return ZoopickNotification.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .payload(objectMapper.convertValue(command.payload(), new TypeReference<>() {
                }))
                .type(command.payload().type())
                .build();
    }
}
