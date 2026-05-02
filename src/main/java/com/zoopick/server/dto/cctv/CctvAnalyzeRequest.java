package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CctvAnalyzeRequest {
    @JsonProperty("job_id")
    private Long jobId;

    @JsonProperty("callback_url")
    private String callbackUrl;

    private List<VideoInfo> videos;
}
