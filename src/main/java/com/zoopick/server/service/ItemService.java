package com.zoopick.server.service;

import com.zoopick.server.dto.item.ItemDetailResponse;
import com.zoopick.server.dto.item.ItemRequest;
import com.zoopick.server.dto.item.ItemResponse;
import com.zoopick.server.entity.Item;
import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public ItemResponse registerItem(ItemRequest request) {

        Item item = Item.builder()
                .title(request.getTitle())
                .type(request.getType())
                .category(request.getCategory())
                .color(request.getColor())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .latitude(request.getLocation().getLat())
                .longitude(request.getLocation().getLng())
                .reportedAt(request.getReportedAt())
                .status("REPORTED")
                .build();

        Item savedItem = itemRepository.save(item);

        return ItemResponse.builder()
                .itemId(savedItem.getId())
                .title(savedItem.getTitle())
                .status(savedItem.getStatus())
                .message("신고가 접수되었습니다. 매칭 분석이 진행 중입니다.")
                .build();
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(item -> ItemResponse.builder()
                        .itemId(item.getId())
                        .title(item.getTitle())
                        .status(item.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemDetailResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> DataNotFoundException.from("물품", id));

        // 500 에러 (NPE) 방지: 신고자가 없을 수도 있으므로 null 체크
        Long reporterId = (item.getReporter() != null) ? item.getReporter().getId() : null;

        return ItemDetailResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .category(item.getCategory())
                .title(item.getTitle())
                .description(item.getDescription())
                .color(item.getColor())
                .imageUrl(item.getImageUrl())
                .locationName(item.getLocationName())
                .reportedAt(item.getReportedAt())
                .lat(item.getLatitude())
                .lng(item.getLongitude())
                .reporterId(reporterId)
                .build();
    }
}