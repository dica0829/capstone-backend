package com.zoopick.server.chat.mapper;

import com.zoopick.server.chat.dto.MessageRecord;
import com.zoopick.server.chat.entity.ChatMessage;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@Component
@NullMarked
public class ChatMessageMapper {
    public MessageRecord toMessageRecord(ChatMessage chatMessage) {
        return MessageRecord.builder()
                .senderId(chatMessage.getSender().getId())
                .senderNickname(chatMessage.getSender().getNickname())
                .message(chatMessage.getContent())
                .sentAt(chatMessage.getSentAt())
                .readAt(chatMessage.getReadAt())
                .build();
    }
}
