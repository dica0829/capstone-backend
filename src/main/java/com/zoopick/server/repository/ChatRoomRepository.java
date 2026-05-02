package com.zoopick.server.repository;

import com.zoopick.server.entity.ChatRoom;
import com.zoopick.server.entity.User;
import com.zoopick.server.exception.DataNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    default ChatRoom findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> DataNotFoundException.from("채팅방", id));
    }

    List<ChatRoom> findByOwnerOrFinder(User owner, User finder);

    default List<ChatRoom> findByParticipant(User user) {
        return findByOwnerOrFinder(user, user);
    }

    Optional<ChatRoom> findByOwnerOrFinderAndItemIdIs(User owner, User finder, long itemId);

    default Optional<ChatRoom> findByParticipantAndItemIdIs(User user, long itemId) {
        return findByOwnerOrFinderAndItemIdIs(user, user, itemId);
    }
}
