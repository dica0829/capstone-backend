package com.zoopick.server.chat.mapper;

import com.zoopick.server.chat.dto.ChatRoomRecord;
import com.zoopick.server.chat.entity.ChatMessage;
import com.zoopick.server.chat.entity.ChatRoom;
import com.zoopick.server.item.entity.Item;
import com.zoopick.server.item.entity.ItemStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@NullMarked
public class ChatRoomMapper {
    public ChatRoomRecord toChatRoomRecord(ChatRoom chatRoom, @Nullable ChatMessage lastMessage, long unreadCount) {
        Optional<ChatMessage> messageOptional = Optional.ofNullable(lastMessage);
        return ChatRoomRecord.builder()
                .roomId(chatRoom.getId())
                .status(chatRoom.getStatus())
                .ownerNickname(chatRoom.getOwner().getNickname())
                .finderNickname(chatRoom.getFinder().getNickname())
                .itemName(resolveItemDetail(chatRoom.getItem()))
                .itemId(resolveItemId(chatRoom.getItem()))
                .itemStatus(resolveItemStatus(chatRoom.getItem()))
                .unreadCount(unreadCount)
                .updateTime(messageOptional.map(ChatMessage::getSentAt).orElse(null))
                .lastMessage(messageOptional.map(ChatMessage::getContent).orElse(""))
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

    @Nullable
    private ItemStatus resolveItemStatus(@Nullable Item item) {
        if (item == null)
            return null;
        return item.getStatus();
    }
}
