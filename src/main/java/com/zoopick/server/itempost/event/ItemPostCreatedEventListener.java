package com.zoopick.server.itempost.event;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.auth.repository.UserRepository;
import com.zoopick.server.item.entity.Item;
import com.zoopick.server.item.entity.ItemType;
import com.zoopick.server.itempost.entity.ItemPost;
import com.zoopick.server.itempost.repository.ItemPostRepository;
import com.zoopick.server.metadata.entity.Building;
import com.zoopick.server.notification.SendNotificationCommand;
import com.zoopick.server.notification.payload.ItemReportedPayload;
import com.zoopick.server.notification.service.NotificationService;
import com.zoopick.server.timetable.entity.DayOfWeek;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@NullMarked
public class ItemPostCreatedEventListener {
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ItemPostRepository itemPostRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationToTimetableMatchingUsers(ItemPostCreatedEvent event) {
        ItemPost itemPost = itemPostRepository.findByIdOrThrow(event.itemPostId());
        User reporter = itemPost.getUser();
        Item item = itemPost.getItem();
        Building building = item.getReportedBuilding();
        LocalDateTime reportedAt = item.getReportedAt();
        List<User> users = userRepository.findUsersWithPrimaryTimetableAt(
                building,
                DayOfWeek.from(reportedAt.getDayOfWeek()),
                reportedAt.toLocalTime(),
                reporter
        );

        notificationService.send(users, new SendNotificationCommand(
                reporter.getNickname(),
                getNotificationBodyByItemType(item.getType()),
                new ItemReportedPayload(
                        item.getId(), item.getDisplayName(), itemPost.getId()
                )
        ));
    }

    private String getNotificationBodyByItemType(ItemType itemType) {
        if (itemType == ItemType.LOST)
            return "분실물이 있어요!";
        return "분실물이 발견되었어요!";
    }
}
