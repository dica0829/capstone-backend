package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CctvCompletedCallback {
    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("total_seconds")
    private Integer totalSeconds;

    @JsonProperty("total_detections")
    private Integer totalDetections;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("completed_at")
    private LocalDateTime completedAt;

    @JsonProperty("duration_ms")
    private Long durationMs;
}
