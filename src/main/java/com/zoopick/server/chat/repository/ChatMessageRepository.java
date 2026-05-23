package com.zoopick.server.chat.repository;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.chat.dto.MessageFilter;
import com.zoopick.server.chat.entity.ChatMessage;
import com.zoopick.server.chat.entity.ChatRoom;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, JpaSpecificationExecutor<ChatMessage> {
    static Specification<ChatMessage> after(LocalDateTime start) {
        return (root, query, criteriaBuilder) -> {
            if (start == null)
                return null;
            return criteriaBuilder.greaterThanOrEqualTo(root.get("sentAt"), start);
        };
    }

    static Specification<ChatMessage> before(LocalDateTime end) {
        return (root, query, criteriaBuilder) -> {
            if (end == null)
                return null;
            return criteriaBuilder.lessThan(root.get("sentAt"), end);
        };
    }

    static Specification<ChatMessage> applyFilter(MessageFilter filter) {
        if (filter == null)
            return Specification.unrestricted();
        return Specification.where(after(filter.getStartTime()))
                .and(before(filter.getEndTime()));
    }

    List<ChatMessage> findByRoomAndSenderIsNot(@NotNull ChatRoom room, @NotNull User sender);

    List<ChatMessage> findByRoomOrderBySentAt(@NotNull ChatRoom room, Specification<ChatMessage> chatMessageSpecification);
}
