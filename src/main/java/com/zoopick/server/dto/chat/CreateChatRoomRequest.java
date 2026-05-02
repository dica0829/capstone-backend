package com.zoopick.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomRequest {
    @JsonProperty("item_id")
    private Long itemId;
    @JsonProperty("counterpart_id")
    private Long counterpartId;
}
