package com.zoopick.server.chat.dto;

import com.zoopick.server.chat.entity.ChatRoomStatus;

public enum ChatRoomCloseReason {
    RETURNED(ChatRoomStatus.RESOLVED_RETURNED),
    ABANDONED(ChatRoomStatus.RESOLVED_ABANDONED);

    private final ChatRoomStatus chatRoomStatus;

    ChatRoomCloseReason(ChatRoomStatus chatRoomStatus) {
        this.chatRoomStatus = chatRoomStatus;
    }

    public ChatRoomStatus toChatRoomStatus() {
        return chatRoomStatus;
    }
}
