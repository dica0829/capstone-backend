package com.zoopick.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Controller
@NullMarked
@RequiredArgsConstructor
public class HomeController {
    private static final String CCTV_ADMIN_BASE_URL_PLACEHOLDER = "${ZOOPICK_BASE_URL}";

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Value("${zoopick.base-url}")
    private String baseUrl;

    @GetMapping("/")
    public String redirectToSwagger() {
        return "redirect:/swagger-ui/index.html";
    }

    @ResponseBody
    @GetMapping(value = "/cctv-admin.html", produces = MediaType.TEXT_HTML_VALUE)
    public String cctvAdmin() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:/static/cctv-admin.html");
        try (InputStream inputStream = resource.getInputStream()) {
            String html = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            return html.replace(CCTV_ADMIN_BASE_URL_PLACEHOLDER, objectMapper.writeValueAsString(baseUrl));
        }
    }
}
