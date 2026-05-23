package com.zoopick.server.item.event;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.chat.entity.ChatRoom;
import com.zoopick.server.chat.entity.ChatRoomStatus;
import com.zoopick.server.chat.repository.ChatRoomRepository;
import com.zoopick.server.exception.InternalServerException;
import com.zoopick.server.item.entity.Item;
import com.zoopick.server.item.repository.ItemRepository;
import com.zoopick.server.notification.SendNotificationCommand;
import com.zoopick.server.notification.payload.ItemReturnedPayload;
import com.zoopick.server.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@NullMarked
public class ItemReturnedEventListener {
    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleItemReturned(ItemReturnedEvent event) {
        Item item = itemRepository.findByIdOrThrow(event.itemId());

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByItem(item);
        User owner = chatRooms.stream()
                .findAny()
                .map(ChatRoom::getOwner)
                .orElseThrow(() -> new InternalServerException("chat_room.owner is null!"));
        List<User> finders = chatRooms.stream()
                .map(ChatRoom::getFinder)
                .toList();

        chatRooms.forEach(chatRoom -> chatRoom.setStatus(ChatRoomStatus.RESOLVED_RETURNED));

        notificationService.send(finders, new SendNotificationCommand(
                owner.getNickname(),
                "아이템을 되찾았어요! 도움을 주셔서 감사합니다",
                new ItemReturnedPayload(
                        event.itemId(), item.getDisplayName(), owner.getNickname()
                )
        ));
    }
}
