package com.zoopick.server.chat.service;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.auth.repository.UserRepository;
import com.zoopick.server.chat.ChatRoomParticipantHelper;
import com.zoopick.server.chat.dto.*;
import com.zoopick.server.chat.entity.ChatMessage;
import com.zoopick.server.chat.entity.ChatRoom;
import com.zoopick.server.chat.entity.ChatRoomStatus;
import com.zoopick.server.chat.mapper.ChatMessageMapper;
import com.zoopick.server.chat.mapper.ChatRoomMapper;
import com.zoopick.server.chat.repository.ChatMessageRepository;
import com.zoopick.server.chat.repository.ChatRoomRepository;
import com.zoopick.server.exception.BadRequestException;
import com.zoopick.server.item.entity.Item;
import com.zoopick.server.item.entity.ItemStatus;
import com.zoopick.server.item.entity.ItemType;
import com.zoopick.server.item.repository.ItemRepository;
import com.zoopick.server.item.service.ItemService;
import com.zoopick.server.notification.SendNotificationCommand;
import com.zoopick.server.notification.payload.ChatMessagePayload;
import com.zoopick.server.notification.payload.QrScannedPayload;
import com.zoopick.server.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@NullMarked
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationService notificationService;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatRoomMapper chatRoomMapper;
    private final ItemService itemService;

    @Transactional
    public CreateChatRoomResult createChatRoom(long requesterId, CreateChatRoomRequest createChatRoomRequest) {
        if (Objects.equals(requesterId, createChatRoomRequest.getCounterpartId()))
            throw new BadRequestException("잘못된 요청입니다.", "Requester and counterpart is same : " + requesterId);

        long itemId = createChatRoomRequest.getItemId();
        Item item = itemRepository.findByIdOrThrow(itemId);
        User requester = userRepository.findByIdOrThrow(requesterId);
        User counterpart = userRepository.findByIdOrThrow(createChatRoomRequest.getCounterpartId());

        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findOpenByParticipantAndItem(requesterId, itemId);
        if (existingChatRoom.isPresent()) {
            return new CreateChatRoomResult(false, chatRoomMapper.toChatRoomRecord(existingChatRoom.get(), null, 0));
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .item(item)
                .owner(ChatRoomParticipantHelper.resolveOwner(item.getType(), requester, counterpart))
                .finder(ChatRoomParticipantHelper.resolveFinder(item.getType(), requester, counterpart))
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return new CreateChatRoomResult(true, chatRoomMapper.toChatRoomRecord(savedChatRoom, null, 0));
    }

    @Transactional
    public CreateChatRoomResult createChatRoomByOwner(long requesterId, long ownerId) {
        User requester = userRepository.findByIdOrThrow(requesterId);
        User owner = userRepository.findByIdOrThrow(ownerId);

        Item item = itemService.createEmptyItem(requester, ItemType.FOUND, ItemStatus.REPORTED);
        ChatRoom chatRoom = ChatRoom.builder()
                .owner(owner)
                .finder(requester)
                .item(item)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        notificationService.send(owner, new SendNotificationCommand(
                requester.getNickname(),
                "분실물을 발견했어요!",
                new QrScannedPayload(savedChatRoom.getId(), requester.getNickname())
        ));
        return new CreateChatRoomResult(true, chatRoomMapper.toChatRoomRecord(savedChatRoom, null, 0));
    }

    public FindChatRoomResult findChatRoom(long userId, long itemId) {
        Optional<Long> chatRoomId = chatRoomRepository.findOpenByParticipantAndItem(userId, itemId)
                .map(ChatRoom::getId);

        return new FindChatRoomResult(chatRoomId.isPresent(), chatRoomId.orElse(0L));
    }

    public ListChatRoomResult getChatRooms(long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipant(userId);
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getId)
                .toList();
        return new ListChatRoomResult(chatRoomIds);
    }

    public ChatRoomRecord getChatRoom(long userId, long chatRoomId) {
        User user = userRepository.findByIdOrThrow(userId);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        User counterpart = ChatRoomParticipantHelper.resolveCounterpart(chatRoom, user);
        ChatRoomParticipantHelper.verifyParticipant(chatRoom, user);
        ChatMessage lastMessage = chatMessageRepository.findFirstByRoomOrderBySentAtDesc(chatRoom);
        long unreadCount = chatMessageRepository.countByRoomAndSenderAndReadAtIsNull(chatRoom, counterpart);

        return chatRoomMapper.toChatRoomRecord(chatRoom, lastMessage, unreadCount);
    }

    public List<Long> getParticipants(long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        return List.of(chatRoom.getOwner().getId(), chatRoom.getFinder().getId());
    }

    public ListMessagesResult getMessages(long userId, long chatRoomId, @Nullable MessageFilter filter) {
        User user = userRepository.findByIdOrThrow(userId);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        User counterpart = ChatRoomParticipantHelper.resolveCounterpart(chatRoom, user);
        ChatRoomParticipantHelper.verifyParticipant(chatRoom, user);

        List<ChatMessage> messages = chatMessageRepository.findByRoomOrderBySentAt(chatRoom, ChatMessageRepository.applyFilter(filter));
        List<MessageRecord> messageRecords = messages.stream()
                .map(chatMessageMapper::toMessageRecord)
                .toList();
        long unreadCount = messages.stream()
                .filter(message -> message.getSender().getId().equals(counterpart.getId()) && message.getReadAt() == null)
                .count();
        ChatRoomRecord chatRoomRecord = chatRoomMapper.toChatRoomRecord(chatRoom, messages.get(messages.size() - 1), unreadCount);
        return new ListMessagesResult(chatRoomRecord, messageRecords);
    }

    private MessageContext sendMessage(long senderId, long chatRoomId, String message) {
        User sender = userRepository.findByIdOrThrow(senderId);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        ChatRoomParticipantHelper.verifyParticipant(chatRoom, sender);
        if (chatRoom.getStatus() != ChatRoomStatus.OPEN)
            throw new BadRequestException("이미 종료된 채팅방입니다.", chatRoomId + " is closed");

        User receiver = ChatRoomParticipantHelper.resolveReceiver(chatRoom, sender);
        ChatMessage chatMessage = ChatMessage.builder()
                .room(chatRoom)
                .sender(sender)
                .content(message)
                .build();
        chatMessageRepository.save(chatMessage);
        return new MessageContext(sender, receiver, chatRoom);
    }

    @Transactional
    public void sendMessageWithNotification(long senderId, long chatRoomId, String message) {
        MessageContext context = sendMessage(senderId, chatRoomId, message);
        SendNotificationCommand command = new SendNotificationCommand(
                context.sender().getNickname(),
                message,
                ChatMessagePayload.of(context.chatRoom(), context.sender(), message)
        );
        notificationService.send(context.receiver(), command);
    }

    @Transactional
    public void sendMessageWithoutNotification(long senderId, long chatRoomId, String message) {
        MessageContext context = sendMessage(senderId, chatRoomId, message);
        notificationService.storeNotification(context.receiver().getId(), ChatMessagePayload.of(
                context.chatRoom(), context.sender(), message)
        );
    }

    @Transactional
    public void readChatMessages(long userId, long chatRoomId) {
        User user = userRepository.findByIdOrThrow(userId);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        ChatRoomParticipantHelper.verifyParticipant(chatRoom, user);

        List<ChatMessage> messages = chatMessageRepository.findByRoomAndSenderIsNot(chatRoom, user);
        messages.forEach(message -> message.setReadAt(LocalDateTime.now()));
        chatMessageRepository.saveAll(messages);
        notificationService.markAllChatsAsRead(userId, chatRoomId);
    }

    @Transactional
    public void closeChatRoom(long userId, long chatRoomId, ChatRoomCloseReason reason) {
        User sender = userRepository.findByIdOrThrow(userId);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        ChatRoomParticipantHelper.verifyParticipant(chatRoom, sender);

        User receiver = ChatRoomParticipantHelper.resolveReceiver(chatRoom, sender);
        if (chatRoom.getStatus() != ChatRoomStatus.OPEN)
            throw new BadRequestException("이미 닫힌 채팅방입니다.", chatRoomId + " is already closed.");

        chatRoom.setStatus(reason.toChatRoomStatus());
        chatRoom.setResolvedBy(sender);
        chatRoom.setResolvedAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        SendNotificationCommand command = new SendNotificationCommand(
                "Zoopick",
                "채팅방이 닫혔습니다.",
                ChatMessagePayload.of(
                        chatRoom, sender, "채팅방이 닫혔습니다."
                )
        );
        notificationService.send(receiver, command);
    }

    @Transactional
    public void reopenChatRoom(long userId, long chatRoomId) {
        User sender = userRepository.findByIdOrThrow(userId);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        ChatRoomParticipantHelper.verifyParticipant(chatRoom, sender);

        User receiver = ChatRoomParticipantHelper.resolveReceiver(chatRoom, sender);
        if (chatRoom.getStatus() == ChatRoomStatus.OPEN)
            throw new BadRequestException("이미 열린 채팅방입니다.", chatRoomId + " is already opened.");

        chatRoom.setStatus(ChatRoomStatus.OPEN);
        chatRoom.setResolvedBy(null);
        chatRoom.setResolvedAt(null);
        chatRoomRepository.save(chatRoom);

        SendNotificationCommand command = new SendNotificationCommand(
                "Zoopick",
                "채팅방이 열렸습니다.",
                ChatMessagePayload.of(
                        chatRoom, sender, "채팅방이 열렸습니다."
                )
        );
        notificationService.send(receiver, command);
    }

    private record MessageContext(User sender, User receiver, ChatRoom chatRoom) {

    }
}
