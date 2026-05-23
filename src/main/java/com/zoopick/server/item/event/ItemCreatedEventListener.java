package com.zoopick.server.item.event;

import com.zoopick.server.cctv.service.CctvMatchService;
import com.zoopick.server.item.entity.ItemType;
import com.zoopick.server.vision.service.VisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemCreatedEventListener {
    private final VisionService visionService;
    private final CctvMatchService cctvMatchService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleItemCreated(ItemCreatedEvent event) {
        visionService.analyzeImage(event.itemId()); //이미지 분석 실행

        if (event.itemType() == ItemType.LOST) // ItemType이 Lost인 경우 cctv 분석 실행
            cctvMatchService.matchLostItemsToCctv(event.itemId());
    }
}
