package com.zoopick.server.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListMessagesResult {
    @JsonProperty("chat_room")
    private ChatRoomRecord chatRoom;
    private List<MessageRecord> messages;
}
