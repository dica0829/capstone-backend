package com.zoopick.server.notification.event;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.zoopick.server.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@NullMarked
public class NotificationDispatchEventListener {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationDispatchRequestedEvent event) {
        List<FcmMessageRequest> messageRequests = event.messages();
        List<Message> messages = messageRequests.stream()
                .filter(FcmMessageRequest::hasToken)
                .map(this::toMessage)
                .toList();

        int unspentCount = messageRequests.size() - messages.size();
        if (unspentCount > 0)
            log.warn("{} / {} notifications could not be dispatched", unspentCount, messageRequests.size());
        try {
            if (!messages.isEmpty()) {
                BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);
                log.warn("{} notifications failed to send", response.getFailureCount());
            }
        } catch (FirebaseMessagingException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private Message toMessage(FcmMessageRequest request) {
        return Message.builder()
                .putAllData(request.data())
                .setToken(request.token().orElseThrow(() -> DataNotFoundException.from("FCM 토큰", request)))
                .build();
    }
}
