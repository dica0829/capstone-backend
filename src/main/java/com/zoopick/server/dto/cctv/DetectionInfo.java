package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetectionInfo {
    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("detected_at")
    private String detectedAt;

    private Float confidence;

    private String category;

    private String color;

    private List<Float> embedding;

    @JsonProperty("item_snapshot_url")
    private String itemSnapshotUrl;

    @JsonProperty("item_theft_url")
    private String itemTheftUrl;
}
