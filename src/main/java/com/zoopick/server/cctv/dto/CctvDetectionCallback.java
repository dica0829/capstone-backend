package com.zoopick.server.cctv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.item.entity.ItemCategory;
import com.zoopick.server.item.entity.ItemColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CctvDetectionCallback {
    @JsonProperty("detection_id")
    private String detectionId;

    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("detected_at")
    private LocalDateTime detectedAt;

    @JsonProperty("detected_category")
    private ItemCategory detectedCategory;

    @JsonProperty("detected_color")
    private ItemColor detectedColor;

    @JsonProperty("item_snapshot_filename")
    private String itemSnapshotFilename;

    @JsonProperty("moment_snapshot_filename")
    private String momentSnapshotFilename;

    private float[] embedding;
}
