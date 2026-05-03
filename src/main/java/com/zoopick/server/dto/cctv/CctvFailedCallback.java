package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CctvFailedCallback {
    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("analyzed_seconds")
    private Integer analyzedSeconds;

    @JsonProperty("total_seconds")
    private Integer totalSeconds;
}
