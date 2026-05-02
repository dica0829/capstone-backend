package com.zoopick.server.mapper;

import com.zoopick.server.dto.item.ItemPostRecord;
import com.zoopick.server.entity.ItemPost;
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
                .type(itemPost.getItem().getType())
                .status(itemPost.getItem().getStatus())
                .category(itemPost.getItem().getCategory())
                .imageUrl(itemPost.getItem().getImageUrl())
                .buildingId(itemPost.getItem().getReportedBuilding().getId())
                .detailAddress(itemPost.getItem().getLocationName())
                .createdAt(itemPost.getCreatedAt())
                .build();
    }
}
