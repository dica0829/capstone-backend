package com.zoopick.server.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListChatRoomResult {
    private List<Long> chatRoomIds;
}
