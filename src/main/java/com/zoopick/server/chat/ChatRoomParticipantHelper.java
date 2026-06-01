package com.zoopick.server.chat;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.chat.entity.ChatRoom;
import com.zoopick.server.exception.BadRequestException;
import com.zoopick.server.item.entity.ItemType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ChatRoomParticipantHelper {
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
    public static User resolveOwner(ItemType itemType, User requester, User counterpart) {
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
    public static User resolveFinder(ItemType itemType, User requester, User counterpart) {
        if (itemType == ItemType.FOUND)
            return counterpart;
        return requester;
    }

    public static void verifyParticipant(ChatRoom chatRoom, User user) {
        User owner = chatRoom.getOwner();
        User finder = chatRoom.getFinder();
        if (!(owner.getId().equals(user.getId()) || finder.getId().equals(user.getId())))
            throw new BadRequestException("사용자가 포함되지 않은 채팅방입니다.", user.getId() + " is not in chat room " + chatRoom.getId());
    }

    public static User resolveReceiver(ChatRoom chatRoom, User sender) {
        User owner = chatRoom.getOwner();
        User finder = chatRoom.getFinder();
        if (finder.getId().equals(sender.getId()))
            return owner;
        return finder;
    }

    public static User resolveCounterpart(ChatRoom chatRoom, User user) {
        User owner = chatRoom.getOwner();
        User finder = chatRoom.getFinder();
        if (finder.getId().equals(user.getId()))
            return owner;
        return finder;
    }
}
