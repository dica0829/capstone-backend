package com.zoopick.server.notification.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoopick.server.exception.InternalServerException;
import com.zoopick.server.notification.entity.NotificationType;
import com.zoopick.server.notification.payload.NotificationPayload;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@NullMarked
public class NotificationPayloadMapper {
    private final ObjectMapper objectMapper;

    public <T extends NotificationPayload> T toNotificationPayload(JsonNode jsonNode, Class<T> payloadType) {
        try {
            return objectMapper.convertValue(jsonNode, payloadType);
        } catch (IllegalArgumentException exception) {
            throw new InternalServerException("Failed to convert payload " + jsonNode, exception);
        }
    }

    public NotificationPayload toNotificationPayload(Object object, NotificationType type) {
        try {
            Class<? extends NotificationPayload> payloadType = type.getClassType();
            return objectMapper.convertValue(object, payloadType);
        } catch (IllegalArgumentException exception) {
            throw new InternalServerException("Failed to convert payload " + object, exception);
        }
    }
}
