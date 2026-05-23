package com.zoopick.server.image.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUploadResult {
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("original_filename")
    private String originalFilename;
}
