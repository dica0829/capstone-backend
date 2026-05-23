package com.zoopick.server.notification.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
@Schema(
        name = "ItemReturnedPayload",
        description = "ITEM_RETURNED : 분실물 반환 알림 payload"
)
public record ItemReturnedPayload(
        @JsonProperty("item_id")
        @Schema(description = "물품 ID", example = "10")
        long itemId,

        @JsonProperty("item_category")
        @Schema(description = "물품 카테고리", example = "우산")
        String itemCategory,

        @JsonProperty("owner_nickname")
        @Schema(description = "물품 소유자", example = "zoopickUser")
        String ownerNickname
) implements NotificationPayload {
    @Override
    public NotificationType type() {
        return NotificationType.ITEM_RETURNED;
    }

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "item_id", String.valueOf(itemId),
                "item_category", itemCategory,
                "owner_nickname", ownerNickname
        );
    }
}
