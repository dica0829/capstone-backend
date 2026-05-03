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
public class CctvEnqueueResponse {
    @JsonProperty("video_id")
    private Long videoId;

    private boolean queued;

    @JsonProperty("queue_position")
    private Integer queuePosition;

    @JsonProperty("estimated_start_at")
    private LocalDateTime estimatedStartAt;
}
