package com.zoopick.server.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoopick.server.dto.notification.NotificationRequest;
import com.zoopick.server.dto.notification.NotificationResponse;
import com.zoopick.server.entity.User;
import com.zoopick.server.entity.ZoopickNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificationResponse.Data toNotificationResponse(ZoopickNotification notification) {
        NotificationResponse.Data data = new NotificationResponse.Data();
        data.setId(notification.getId());
        data.setType(notification.getType());
        data.setCreatedAt(notification.getCreatedAt());
        Map<String, Object> payload = objectMapper.convertValue(notification.getPayload(), new TypeReference<>() {
        });
        data.setPayload(payload);
        data.setReadAt(notification.getReadAt());

        return data;
    }

    public ZoopickNotification toZoopickNotification(User user, NotificationRequest request) {
        return ZoopickNotification.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .payload(objectMapper.convertValue(request.getPayload(), new TypeReference<>() {
                }))
                .type(request.getType())
                .build();
    }
}
