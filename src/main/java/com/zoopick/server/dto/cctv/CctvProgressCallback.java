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
public class CctvProgressCallback {
    @JsonProperty("video_id")
    private Long videoId;

    private String status;

    @JsonProperty("analyzed_seconds")
    private Integer analyzedSeconds;

    @JsonProperty("total_seconds")
    private Integer totalSeconds;

    @JsonProperty("progress_percent")
    private Double progressPercent;

    @JsonProperty("estimated_completion_at")
    private LocalDateTime estimatedCompletionAt;
}
