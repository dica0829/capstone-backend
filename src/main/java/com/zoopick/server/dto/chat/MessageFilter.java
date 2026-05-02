package com.zoopick.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageFilter {
    @JsonProperty("start_time")
    private LocalDateTime startTime;
    @JsonProperty("end_time")
    private LocalDateTime endTime;
}
