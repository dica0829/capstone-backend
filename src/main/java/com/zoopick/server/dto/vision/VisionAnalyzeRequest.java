package com.zoopick.server.dto.vision;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VisionAnalyzeRequest {
    @JsonProperty("image_url")
    private String imageUrl;
}
