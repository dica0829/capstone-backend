package com.zoopick.server.vision.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisionAnalyzeRequest {
    @JsonProperty("image_url")
    private String imageUrl;
}
