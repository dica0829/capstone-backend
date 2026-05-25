package com.zoopick.server.itempost.mapper;

import com.zoopick.server.itempost.dto.ItemPostRecord;
import com.zoopick.server.itempost.entity.ItemPost;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@NullMarked
public class ItemPostMapper {
    public ItemPostRecord toItemPostRecord(ItemPost itemPost) {
        return ItemPostRecord.builder()
                .id(itemPost.getId())
                .itemId(itemPost.getItem().getId())
                .title(itemPost.getTitle())
                .name(itemPost.getItem().getDisplayName())
                .description(itemPost.getDescription())
                .type(itemPost.getItem().getType())
                .status(itemPost.getItem().getStatus())
                .category(itemPost.getItem().getCategory())
                .color(itemPost.getItem().getColor())
                .reporterId(itemPost.getUser().getId())
                .imageUrl(itemPost.getItem().getImageUrl())
                .buildingId(itemPost.getItem().getReportedBuilding().getId())
                .detailAddress(itemPost.getItem().getLocationName())
                .createdAt(itemPost.getCreatedAt())
                .reportedAt(itemPost.getItem().getReportedAt())
                .nickname(itemPost.getUser().getNickname())
                .build();
    }
}
