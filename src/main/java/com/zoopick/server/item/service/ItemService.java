package com.zoopick.server.item.service;

import com.zoopick.server.auth.entity.User;
import com.zoopick.server.item.CreateItemCommand;
import com.zoopick.server.item.entity.Item;
import com.zoopick.server.item.entity.ItemStatus;
import com.zoopick.server.item.entity.ItemType;
import com.zoopick.server.item.event.ItemCreatedEvent;
import com.zoopick.server.item.event.ItemReturnedEvent;
import com.zoopick.server.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@NullMarked
public class ItemService {
    private final ItemRepository itemRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Item createEmptyItem(User reporter, ItemType type, ItemStatus status) {
        Item item = Item.builder()
                .reporter(reporter)
                .type(type)
                .status(status)
                .build();
        return itemRepository.save(item);
    }

    @Transactional
    public Item createItem(CreateItemCommand command) {
        Item item = Item.builder()
                .reporter(command.reporter())
                .type(command.type())
                .status(ItemStatus.REPORTED)
                .category(command.category())
                .color(command.color())
                .embedding(null)
                .reportedBuilding(command.building())
                .locationName(command.detailAddress())
                .imageUrl(command.imageUrl())
                .reportedAt(command.reportedAd() != null
                        ? command.reportedAd().atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime()
                        : LocalDateTime.now())
                .build();

        Item savedItem = itemRepository.save(item);
        applicationEventPublisher.publishEvent(new ItemCreatedEvent(savedItem.getId(), item.getType()));
        return savedItem;
    }

    @Transactional
    public void markItemAsReturned(long itemId) {
        Item item = itemRepository.findByIdOrThrow(itemId);
        item.changeStatus(ItemStatus.RETURNED);

        applicationEventPublisher.publishEvent(new ItemReturnedEvent(itemId));
    }
}
