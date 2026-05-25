package com.zoopick.server.item;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.item.entity.ItemCategory;
import com.zoopick.server.item.entity.ItemColor;
import com.zoopick.server.item.entity.ItemType;
import com.zoopick.server.metadata.entity.Building;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record CreateItemCommand(
        User reporter,
        ItemType type,
        ItemCategory category,
        ItemColor color,
        String imageUrl,
        Building building,
        String detailAddress,
        OffsetDateTime reportedAt
) {

}
