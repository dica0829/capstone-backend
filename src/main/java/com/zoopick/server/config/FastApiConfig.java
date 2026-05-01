package com.zoopick.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
public class FastApiConfig {
    private final FastApiProperties fastApiProperties;

    public FastApiConfig(FastApiProperties fastApiProperties) {
        this.fastApiProperties = fastApiProperties;
    }

    @Bean
    public RestClient fastApiRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(fastApiProperties.getVision().getConnectTimeout());
        factory.setReadTimeout(fastApiProperties.getVision().getReadTimeout());

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
