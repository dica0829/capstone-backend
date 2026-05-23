package com.zoopick.server.notification.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
@Schema(
        name = "ItemReportedPayload",
        description = "ITEM_REPORTED : 게시글 등록 알림 payload"
)
public record ItemReportedPayload(
        @JsonProperty("item_id")
        @Schema(description = "물품 ID", example = "10")
        long itemId,

        @JsonProperty("item_name")
        @Schema(description = "물품 이름", example = "인형")
        String itemName,

        @JsonProperty("item_post_id")
        @Schema(description = "게시글 ID", example = "10")
        long itemPostId
) implements NotificationPayload {
    @Override
    public NotificationType type() {
        return NotificationType.ITEM_REPORTED;
    }

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "item_id", String.valueOf(itemId),
                "item_name", itemName,
                "item_post_id", String.valueOf(itemPostId)
        );
    }
}
