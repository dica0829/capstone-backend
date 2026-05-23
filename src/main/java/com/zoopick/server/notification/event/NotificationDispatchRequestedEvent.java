package com.zoopick.server.notification.event;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record NotificationDispatchRequestedEvent(List<FcmMessageRequest> messages) {
}
