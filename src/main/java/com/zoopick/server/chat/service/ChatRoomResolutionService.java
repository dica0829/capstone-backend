package com.zoopick.server.chat.service;

import com.zoopick.server.chat.entity.ChatRoomStatus;
import com.zoopick.server.chat.repository.ChatRoomRepository;
import com.zoopick.server.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@NullMarked
public class ChatRoomResolutionService {
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void resolveReturnedItemRooms(Item item) {
        chatRoomRepository.findAllByItemAndStatus(item, ChatRoomStatus.OPEN)
                .forEach(chatRoom -> chatRoom.setStatus(ChatRoomStatus.RESOLVED_RETURNED));
    }
}
