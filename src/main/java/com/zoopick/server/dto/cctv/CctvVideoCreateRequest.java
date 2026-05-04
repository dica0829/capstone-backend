package com.zoopick.server.dto.cctv;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CctvVideoCreateRequest {
    @NotNull
    @JsonProperty("room_id")
    private Long roomId;

    @NotNull
    @JsonProperty("recorded_at")
    private LocalDateTime recordedAt;

    @NotNull
    @JsonProperty("duration_seconds")
    private Integer durationSeconds;

    @NotBlank
    @JsonProperty("video_url")
    private String videoUrl;
}
