package com.zoopick.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.entity.ChatRoomStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomRecord {
    @JsonProperty("room_id")
    private long roomId;
    private ChatRoomStatus status;
    @JsonProperty("owner_nickname")
    private String ownerNickname;
    @JsonProperty("finder_nickname")
    private String finderNickname;
    @JsonProperty("item_name")
    private String itemName;
}
