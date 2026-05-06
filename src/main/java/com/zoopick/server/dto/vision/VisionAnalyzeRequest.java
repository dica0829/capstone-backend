package com.zoopick.server.dto.vision;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VisionAnalyzeRequest {
    @JsonProperty("image_url")
    private String imageUrl;
}
