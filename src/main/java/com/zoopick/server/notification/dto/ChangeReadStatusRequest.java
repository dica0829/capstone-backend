package com.zoopick.server.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChangeReadStatusRequest {
    @NotBlank
    @JsonProperty("notification_ids")
    private List<Long> notificationIds;
}
