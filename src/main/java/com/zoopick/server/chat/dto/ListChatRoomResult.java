package com.zoopick.server.chat.dto;

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
