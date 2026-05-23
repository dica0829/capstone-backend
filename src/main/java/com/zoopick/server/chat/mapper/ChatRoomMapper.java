package com.zoopick.server.chat.mapper;

import com.zoopick.server.chat.dto.ChatRoomRecord;
import com.zoopick.server.chat.entity.ChatRoom;
import com.zoopick.server.item.entity.Item;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
@NullMarked
public class ChatRoomMapper {
    public ChatRoomRecord toChatRoomRecord(ChatRoom chatRoom) {
        return ChatRoomRecord.builder()
                .roomId(chatRoom.getId())
                .status(chatRoom.getStatus())
                .ownerNickname(chatRoom.getOwner().getNickname())
                .finderNickname(chatRoom.getFinder().getNickname())
                .itemName(resolveItemDetail(chatRoom.getItem()))
                .itemId(resolveItemId(chatRoom.getItem()))
                .build();
    }

    private String resolveItemDetail(@Nullable Item item) {
        if (item == null)
            return "";
        return item.getDisplayName();
    }

    @Nullable
    private Long resolveItemId(@Nullable Item item) {
        if (item == null)
            return null;
        return item.getId();
    }
}
