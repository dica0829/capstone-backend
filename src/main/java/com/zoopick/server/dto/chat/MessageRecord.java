package com.zoopick.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRecord {
    @JsonProperty("sender_id")
    private long senderId;
    @JsonProperty("sender_nickname")
    private String senderNickname;
    private String message;
    @JsonProperty("sent_at")
    private LocalDateTime sentAt;
    @JsonProperty("read_at")
    private LocalDateTime readAt;
}
