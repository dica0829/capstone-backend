package com.zoopick.server.cctv.service;

import com.zoopick.server.cctv.CctvMatchCriteriaResolver;
import com.zoopick.server.cctv.entity.CctvDetection;
import com.zoopick.server.cctv.entity.CctvDetectionMatch;
import com.zoopick.server.cctv.event.CreateCctvMatchEvent;
import com.zoopick.server.cctv.repository.CctvDetectionMatchRepository;
import com.zoopick.server.cctv.repository.CctvDetectionRepository;
import com.zoopick.server.config.MatchConfig;
import com.zoopick.server.item.entity.Item;
import com.zoopick.server.item.entity.ItemColor;
import com.zoopick.server.item.repository.ItemRepository;
import com.zoopick.server.itemmatch.dto.SimilarItemResult;
import com.zoopick.server.itempost.entity.ItemPost;
import com.zoopick.server.itempost.repository.ItemPostRepository;
import com.zoopick.server.metadata.entity.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CctvMatchService {
    private final MatchConfig matchConfig;
    private final CctvDetectionRepository cctvDetectionRepository;
    private final CctvDetectionMatchRepository cctvDetectionMatchRepository;
    private final ItemRepository itemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ItemPostRepository itemPostRepository;
    private final CctvMatchCriteriaResolver cctvMatchCriteriaResolver;

    //CCTV가 분석 완료됐을 때 매칭
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void matchCctvToLostItems(Long detectionId) {
        log.info("[CCTV] 매칭 시작 ID: {}", detectionId);
        CctvDetection cctvDetection = cctvDetectionRepository.findById(detectionId).orElse(null);
        if (cctvDetection == null)
            return;

        Vector embedding = Vector.of(cctvDetection.getEmbedding());

        List<SimilarItemResult> similarItems = cctvDetectionMatchRepository.findLostItems(
                embedding,
                cctvDetection.getDetectedCategory().name(),
                cctvDetection.getDetectedAt(),
                matchConfig.getCctvSimilarityThreshold())
                .stream()
                .map(p -> new SimilarItemResult(p.getItemId(), p.getScore()))
                .toList();

        if (similarItems.isEmpty()) {
            log.warn("[CCTV] 매칭된 아이템이 없습니다.");
            return;
        }
        List<Long> itemIds = similarItems.stream()
                .map(SimilarItemResult::getItemId)
                .toList();

        Map<Long, ItemPost> itemPostMap = itemPostRepository.findAllByItemIdsWithItem(itemIds)
                .stream()
                .collect(Collectors.toMap(
                        post -> post.getItem().getId(),
                        post -> post));

        Room detectionRoom = cctvDetection.getCctvVideo().getRoom();
        List<CreateCctvMatchEvent.Entry> entries = new ArrayList<>();

        for (SimilarItemResult s : similarItems) {
            ItemPost itemPost = itemPostMap.get(s.getItemId());
            if (itemPost == null)
                continue;
            Item lostItem = itemPost.getItem();

            List<Long> roomIds = cctvMatchCriteriaResolver.resolveRoomIds(lostItem);
            if (!roomIds.contains(detectionRoom.getId())) {
                log.debug("[CCTV] 아이템 검색 범위 외 강의실 탐지 ID: {}", lostItem.getId());
                continue;
            }

            if (!cctvDetectionMatchRepository.existsByCctvDetectionAndItem(cctvDetection, lostItem)) {
                float finalScore = calculateBonusScore(s.getScore(), lostItem.getColor(), cctvDetection.getDetectedColor());
                CctvDetectionMatch savedMatch = cctvDetectionMatchRepository.save(CctvDetectionMatch.builder()
                        .score(finalScore)
                        .item(lostItem)
                        .cctvDetection(cctvDetection)
                        .build());
                log.info("CCTV 매칭된 아이템 ID: {}", lostItem.getId());
                entries.add(new CreateCctvMatchEvent.Entry(
                        savedMatch.getId(),
                        savedMatch.getScore(),
                        lostItem.getId(),
                        lostItem.getReporter().getId(),
                        itemPost.getTitle(),
                        detectionRoom.getName()));
            }
        }

        if (!entries.isEmpty()) {
            eventPublisher.publishEvent(new CreateCctvMatchEvent(entries));
        }
        log.info("[CCTV] 매칭 종료 ID: {}", detectionId);
    }

    //사용자가 글을 올렸을 때 매칭
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void matchLostItemsToCctv(Long lostItemId) {
        log.info("[CCTV] 매칭 시작 ID: {}", lostItemId);
        Item lostItem = itemRepository.findByIdOrThrow(lostItemId);
        ItemPost lostItemPost = itemPostRepository.findByItem(lostItem);

        Vector embedding = Vector.of(lostItem.getEmbedding());

        if (lostItem.getReportedAt() == null) {
            log.info("[CCTV] 분실 시각이 없어 매칭을 스킵합니다. ID: {}", lostItemId);
            return;
        }

        List<Long> roomIds = cctvMatchCriteriaResolver.resolveRoomIds(lostItem);
        if (roomIds.isEmpty()) {
            log.info("[CCTV] 매칭 가능한 장소 정보가 없습니다. ID: {}", lostItemId);
            return;
        }

        List<SimilarItemResult> similarItems = cctvDetectionMatchRepository.findDetections(
                embedding,
                lostItem.getCategory().name(),
                roomIds,
                lostItem.getReportedAt(),
                matchConfig.getSimilarityThreshold())
                .stream()
                .map(p -> new SimilarItemResult(p.getItemId(), p.getScore()))
                .toList();

        if (similarItems.isEmpty()) {
            log.warn("[CCTV] 매칭된 Detection이 없습니다.");
            return;
        }
        Map<Long, CctvDetection> detectionMap = cctvDetectionRepository.findAllById(
                similarItems.stream().map(SimilarItemResult::getItemId).toList())
                .stream()
                .collect(Collectors.toMap(CctvDetection::getId, i -> i));

        List<CreateCctvMatchEvent.Entry> entries = new ArrayList<>();

        for (SimilarItemResult s : similarItems) {
            CctvDetection foundItemInDb = detectionMap.get(s.getItemId());
            if (foundItemInDb == null)
                continue;

            if (!cctvDetectionMatchRepository.existsByCctvDetectionAndItem(foundItemInDb, lostItem)) {
                float finalScore = calculateBonusScore(s.getScore(), lostItem.getColor(), foundItemInDb.getDetectedColor());
                CctvDetectionMatch savedMatch = cctvDetectionMatchRepository.save(CctvDetectionMatch.builder()
                        .score(finalScore)
                        .item(lostItem)
                        .cctvDetection(foundItemInDb)
                        .build());
                log.info("[CCTV] 매칭된 Detection ID: {}", foundItemInDb.getId());
                Room foundRoom = foundItemInDb.getCctvVideo().getRoom();
                entries.add(new CreateCctvMatchEvent.Entry(
                        savedMatch.getId(),
                        savedMatch.getScore(),
                        lostItem.getId(),
                        lostItem.getReporter().getId(),
                        lostItemPost.getTitle(),
                        foundRoom.getName()));
            }
        }

        if (!entries.isEmpty()) {
            eventPublisher.publishEvent(new CreateCctvMatchEvent(entries));
        }
        log.info("[CCTV] 매칭 종료 ID: {}", lostItemId);
    }

    private float calculateBonusScore(double baseScore, ItemColor itemColor, ItemColor detectionColor) {
        float finalScore = (float) baseScore;
        // 두 컬러가 모두 존재하고 일치할 경우 가산점 적용
        if (itemColor == detectionColor) {
            finalScore *= matchConfig.getColorBonus();
        }
        return Math.min(finalScore, 1.0f);
    }
}