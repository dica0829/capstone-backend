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
public class CctvEnqueueRequest {
    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("video_path")
    private String videoPath;

    @JsonProperty("duration_seconds")
    private Integer durationSeconds;

    @JsonProperty("recorded_at")
    private LocalDateTime recordedAt;

    @JsonProperty("callback_base_url")
    private String callbackBaseUrl;
}