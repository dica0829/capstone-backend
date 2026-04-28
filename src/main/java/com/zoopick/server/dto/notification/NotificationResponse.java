package com.zoopick.server.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.entity.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class NotificationResponse extends CommonResponse<List<NotificationResponse.Data>> {
    public static NotificationResponse success(List<Data> data) {
        return new NotificationResponse(true, data, null);
    }

    public NotificationResponse(boolean success, List<Data> data, String error) {
        super(success, data, error);
    }

    @Getter
    @Setter
    public static class Data {
        private long id;
        private NotificationType type;
        private Map<String, Object> payload;
        @JsonProperty("read_at")
        private LocalDateTime readAt;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }
}
