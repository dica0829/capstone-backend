package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CctvCallbackRequest {
    @JsonProperty("job_id")
    private Long jobId;

    private String status;

    private List<DetectionInfo> detections;

    @JsonProperty("error_message")
    private String errorMessage;
}
