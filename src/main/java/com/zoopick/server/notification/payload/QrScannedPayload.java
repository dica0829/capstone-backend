package com.zoopick.server.notification.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public record QrScannedPayload(
        @JsonProperty("room_id")
        @Schema(description = "채팅방 ID", example = "123")
        long roomId,
        @JsonProperty("finder_nickname")
        @Schema(description = "찾은 사람 닉네임", example = "zoopickUser")
        String finderName
) implements NotificationPayload {
    @Override
    public NotificationType type() {
        return NotificationType.QR_SCANNED;
    }

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "room_id", String.valueOf(roomId),
                "finder_name", finderName
        );
    }
}
