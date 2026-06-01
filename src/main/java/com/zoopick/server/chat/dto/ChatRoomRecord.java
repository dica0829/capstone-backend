package com.zoopick.server.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.chat.entity.ChatRoomStatus;
import com.zoopick.server.item.entity.ItemStatus;
import lombok.*;

import java.time.LocalDateTime;

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
    @JsonProperty("item_id")
    private Long itemId;
    @JsonProperty("item_status")
    private ItemStatus itemStatus;
    @JsonProperty("unread_count")
    private Long unreadCount;
    @JsonProperty("update_time")
    private LocalDateTime updateTime;
    @JsonProperty("last_message")
    private String lastMessage;
}
