package com.zoopick.server.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateChatRoomByOwnerRequest {
    @JsonProperty("owner_id")
    private Long ownerId;
}
