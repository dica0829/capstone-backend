package com.zoopick.server.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChangeReadStatusRequest {
    @JsonProperty("notification_ids")
    private List<Long> notificationIds;
}
