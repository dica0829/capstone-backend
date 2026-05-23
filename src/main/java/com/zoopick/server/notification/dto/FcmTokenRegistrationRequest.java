package com.zoopick.server.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenRegistrationRequest {
    @NotBlank
    private String token;
}
