package com.zoopick.server.mapper;

import com.zoopick.server.dto.chat.MessageRecord;
import com.zoopick.server.entity.ChatMessage;
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
