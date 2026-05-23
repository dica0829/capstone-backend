package com.zoopick.server.item.event;

import com.zoopick.server.item.entity.ItemType;

public record ItemCreatedEvent(Long itemId, ItemType itemType) {
}
