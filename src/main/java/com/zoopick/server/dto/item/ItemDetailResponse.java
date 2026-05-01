package com.zoopick.server.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ItemDetailResponse {
    private Long id;
    private String type;
    private String category;
    private String title;
    private String description;
    private String color;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("reported_at")
    private OffsetDateTime reportedAt;

    private Double lat;
    private Double lng;

    @JsonProperty("reporter_id")
    private Long reporterId;
}