package com.zoopick.server.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.zoopick.server.dto.chat.*;
import com.zoopick.server.dto.notification.SendNotificationRequest;
import com.zoopick.server.entity.*;
import com.zoopick.server.exception.BadRequestException;
import com.zoopick.server.mapper.ChatMessageMapper;
import com.zoopick.server.mapper.ChatRoomMapper;
import com.zoopick.server.repository.ChatMessageRepository;
import com.zoopick.server.repository.ChatRoomRepository;
import com.zoopick.server.repository.ItemRepository;
import com.zoopick.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    public CreateChatRoomResult createChatRoom(String requesterEmail, CreateChatRoomRequest createChatRoomRequest) {
        long itemId = createChatRoomRequest.getItemId();
        Item item = itemRepository.findByIdOrThrow(itemId);
        User requester = userRepository.findBySchoolEmailOrThrow(requesterEmail);
        User counterpart = userRepository.findByIdOrThrow(createChatRoomRequest.getCounterpartId());
        ChatRoom chatRoom = ChatRoom.builder()
                .item(item)
                .owner(resolveOwner(item.getType(), requester, counterpart))
                .finder(resolveFinder(item.getType(), requester, counterpart))
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return new CreateChatRoomResult(savedChatRoom.getId());
    }

    /**
     * 이 <b>요청을 보낸 사람은 게시글을 보고 요청</b>을 보낸다.<br/>
     * <p>item type이 <b>{@link ItemType#FOUND}</b>일 경우 게시글을 작성한 사람은 발견한 사람.
     * <u>이 요청을 보낸 사람이 분실물의 주인</u>이다.<p/>
     * <p>item type이 <b>{@link ItemType#LOST}</b>일 경우 게시글을 작성한 사람은 분실물의 주인.
     * <u>이 요청의 상대가 분실물의 주인</u>이다.<p/>
     *
     * @param itemType    게시글의 종류 (FOUND | LOST)
     * @param requester   게시글을 보고 채팅을 요청한 사람
     * @param counterpart 게시글 작성자
     * @return 분실물의 주인
     */
    private User resolveOwner(ItemType itemType, User requester, User counterpart) {
        if (itemType == ItemType.FOUND)
            return requester;
        return counterpart;
    }

    /**
     * 이 <b>요청을 보낸 사람은 게시글을 보고 요청</b>을 보낸다.<br/>
     * <p>item type이 <b>{@link ItemType#FOUND}</b>일 경우 게시글을 작성한 사람은 발견한 사람.
     * <u>이 요청의 상대가 발견한 사람</u>이다.<p/>
     * <p>item type이 <b>{@link ItemType#LOST}</b>일 경우 게시글을 작성한 사람은 분실물의 주인.
     * <u>이 요청을 보낸 사람이 발견한 사람</u>이다.<p/>
     *
     * @param itemType    게시글의 종류 (FOUND | LOST)
     * @param requester   게시글을 보고 채팅을 요청한 사람
     * @param counterpart 게시글 작성자
     * @return 분실물을 발견한 사람
     */
    private User resolveFinder(ItemType itemType, User requester, User counterpart) {
        if (itemType == ItemType.FOUND)
            return counterpart;
        return requester;
    }

    public FindChatRoomResult findChatRoom(String email, long itemId) {
        User user = userRepository.findBySchoolEmailOrThrow(email);
        Optional<Long> chatRoomId = chatRoomRepository.findByParticipantAndItemIdIs(user, itemId)
                .map(ChatRoom::getId);

        return new FindChatRoomResult(chatRoomId.isPresent(), chatRoomId.orElse(0L));
    }

    private void validateParticipant(ChatRoom chatRoom, User user) {
        User owner = chatRoom.getOwner();
        User finder = chatRoom.getFinder();
        if (!(owner.getId().equals(user.getId()) || finder.getId().equals(user.getId())))
            throw new BadRequestException("사용자가 포함되지 않은 채팅방입니다.", user.getId() + " is not in chat room " + chatRoom.getId());
    }

    public ListChatRoomResult getChatRooms(String email) {
        User user = userRepository.findBySchoolEmailOrThrow(email);
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipant(user);
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getId)
                .toList();
        return new ListChatRoomResult(chatRoomIds);
    }

    public ChatRoomRecord getChatRoom(String email, long chatRoomId) {
        User user = userRepository.findBySchoolEmailOrThrow(email);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        validateParticipant(chatRoom, user);

        return chatRoomMapper.toChatRoomRecord(chatRoom);
    }

    public ListMessagesResult getMessages(String email, long chatRoomId, @Nullable MessageFilter filter) {
        User user = userRepository.findBySchoolEmailOrThrow(email);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        validateParticipant(chatRoom, user);

        List<ChatMessage> messages = chatMessageRepository.findAll(ChatMessageRepository.applyFilter(filter));
        List<MessageRecord> messageRecords = messages.stream()
                .map(chatMessageMapper::toMessageRecord)
                .toList();
        ChatRoomRecord chatRoomRecord = chatRoomMapper.toChatRoomRecord(chatRoom);
        return new ListMessagesResult(chatRoomRecord, messageRecords);
    }

    public void sendMessage(String senderEmail, long chatRoomId, String message) throws FirebaseMessagingException {
        User sender = userRepository.findBySchoolEmailOrThrow(senderEmail);
        ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomId);
        validateParticipant(chatRoom, sender);

        User receiver = resolveReceiver(chatRoom, sender);
        ChatMessage chatMessage = ChatMessage.builder()
                .room(chatRoom)
                .sender(sender)
                .content(message)
                .build();
        chatMessageRepository.save(chatMessage);
        Map<String, String> payload = Map.of(
                "room_id", String.valueOf(chatRoomId),
                "sender_nickname", sender.getNickname(),
                "message", message
        );
        SendNotificationRequest request = SendNotificationRequest.builder()
                .title(sender.getNickname())
                .body(message)
                .type(NotificationType.CHAT_MESSAGE)
                .payload(payload)
                .build();
        notificationService.send(receiver, request);
    }

    private User resolveReceiver(ChatRoom chatRoom, User sender) {
        User owner = chatRoom.getOwner();
        User finder = chatRoom.getFinder();
        if (finder.getId().equals(sender.getId()))
            return owner;
        return finder;
    }
}
