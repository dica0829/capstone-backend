package com.zoopick.server.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class ItemRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String type; // "LOST" or "FOUND"

    @NotBlank
    private String category;
    private String color;

    @NotBlank
    private String description;

    @JsonProperty("image_url") // 500 오류 방지
    private String imageUrl;

    @NotNull
    private LocationDto location;

    @NotNull
    @JsonProperty("reported_at")
    private OffsetDateTime reportedAt;

    @Getter
    public static class LocationDto {
        private Double lat;
        private Double lng;
    }
}
