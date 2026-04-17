package com.example.member.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    // 1. JWT 토큰 입력 버튼(Authorize)을 활성화하는 설정
    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "Bearer Authentication";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    // 2. 기존의 API 정렬 로직
    @Bean
    public OpenApiCustomizer sortOperationsBySummary() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths == null) return;
            Paths sortedPaths = new Paths();
            paths.entrySet().stream()
                    .sorted((entry1, entry2) -> {
                        String s1 = entry1.getValue().readOperations().isEmpty() ? ""
                                : entry1.getValue().readOperations().get(0).getSummary();
                        String s2 = entry2.getValue().readOperations().isEmpty() ? ""
                                : entry2.getValue().readOperations().get(0).getSummary();
                        return (s1 == null ? "" : s1).compareTo(s2 == null ? "" : s2);
                    })
                    .forEach(entry -> sortedPaths.addPathItem(entry.getKey(), entry.getValue()));
            openApi.setPaths(sortedPaths);
        };
    }
}