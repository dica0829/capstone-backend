package com.zoopick.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "fastapi")
public class FastApiProperties {
    private String baseUrl;
    private Vision vision;

    @Getter
    @Setter
    public static class Vision {
        private String analyzePath;
        private Duration connectTimeout;
        private Duration readTimeout;
    }
}