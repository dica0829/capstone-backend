package com.zoopick.server.notification.entity;

import com.zoopick.server.notification.payload.*;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public enum NotificationType {
    MATCH_FOUND(MatchFoundPayload.class),
    CCTV_FOUND(CctvFoundPayload.class),
    CHAT_MESSAGE(ChatMessagePayload.class),
    ITEM_RETURNED(ItemReturnedPayload.class),
    THEFT_SUSPECTED(TheftSuspectedPayload.class),
    LOCKER_READY(LockerReadyPayload.class),
    QR_SCANNED(QrScannedPayload.class);

    private final Class<? extends NotificationPayload> classType;

    NotificationType(Class<? extends NotificationPayload> classType) {
        this.classType = classType;
    }
}
