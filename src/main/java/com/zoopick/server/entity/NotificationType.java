package com.zoopick.server.entity;

import java.util.Map;
import java.util.Set;

public enum NotificationType {
    MATCH_FOUND("item_id", "match_id", "score"),
    CHAT_MESSAGE("room_id", "sender_nickname", "message"),
    ITEM_RETURNED("item_id", "item_post_id"),
    THEFT_SUSPECTED("item_id"),
    LOCKER_READY("item_id");

    private final Set<String> allowedPayloadKeys;

    NotificationType(String... allowedPayloadKeys) {
        this.allowedPayloadKeys = Set.of(allowedPayloadKeys);
    }

    public boolean verifyPayload(Map<String, String> payload) {
        return payload.keySet().containsAll(this.allowedPayloadKeys);
    }
}
