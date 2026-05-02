package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoInfo {
    @JsonProperty("video_id")
    private long videoId;

    private String url;

    @JsonProperty("recorded_at")
    private String recordedAt;
}
