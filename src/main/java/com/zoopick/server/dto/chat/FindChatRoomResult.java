package com.zoopick.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FindChatRoomResult {
    private boolean exists;
    @JsonProperty("room_id")
    private long roomId;
}
