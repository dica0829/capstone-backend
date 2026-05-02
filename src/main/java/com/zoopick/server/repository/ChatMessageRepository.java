package com.zoopick.server.repository;

import com.zoopick.server.dto.chat.MessageFilter;
import com.zoopick.server.entity.ChatMessage;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

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
}
