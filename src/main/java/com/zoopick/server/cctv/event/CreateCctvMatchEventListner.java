package com.zoopick.server.cctv.event;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.auth.repository.UserRepository;
import com.zoopick.server.notification.SendNotificationCommand;
import com.zoopick.server.notification.payload.CctvFoundPayload;
import com.zoopick.server.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCctvMatchEventListner {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchCreated(CreateCctvMatchEvent event) {
        List<CreateCctvMatchEvent.Entry> entries = event.entries();
        if (entries.isEmpty()) return;

        List<Long> reporterIds = entries.stream()
                .map(CreateCctvMatchEvent.Entry::reporterUserId)
                .distinct()
                .toList();
        Map<Long, User> reporterMap = userRepository.findAllById(reporterIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        for (CreateCctvMatchEvent.Entry entry : entries) {
            User reporter = reporterMap.get(entry.reporterUserId());
            if (reporter == null) continue;

            notificationService.send(reporter, new SendNotificationCommand(
                    "도난 의심",
                    "회원님이 등록한 %s와 유사한 물건이 %s에서 도난이 의심돼요."
                            .formatted(entry.itemPostTitle(), entry.roomName()),
                    new CctvFoundPayload(entry.lostItemId(), entry.matchId(), entry.score())));
            log.info("FCM 전송 성공 matchId: {}", entry.matchId());
        }
    }
}
