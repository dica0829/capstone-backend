package com.zoopick.server.dto.fcm;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FcmNotificationRequest {
    private String title;
    private String body;
    private Map<String, String> data = Map.of();
}
