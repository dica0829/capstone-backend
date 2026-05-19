package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.entity.ItemCategory;
import com.zoopick.server.entity.ItemColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllDetectionResponse {
    private Long id;
    @JsonProperty("video_id")
    private Long videoId;
    @JsonProperty("room_name")
    private String roomName;
    @JsonProperty("building_name")
    private String buildingName;
    @JsonProperty("detected_at")
    private LocalDateTime detectedAt;
    @JsonProperty("detected_category")
    private ItemCategory detectedCategory;
    @JsonProperty("detected_color")
    private ItemColor detectedColor;
}
