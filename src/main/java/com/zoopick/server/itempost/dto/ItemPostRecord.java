package com.zoopick.server.itempost.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zoopick.server.item.entity.ItemCategory;
import com.zoopick.server.item.entity.ItemColor;
import com.zoopick.server.item.entity.ItemStatus;
import com.zoopick.server.item.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemPostRecord {
    private long id;
    private String title;
    private String description;
    @JsonProperty("item_id")
    private long itemId;
    private ItemType type;
    private ItemStatus status;
    private ItemCategory category;
    private String name;
    private ItemColor color;
    @JsonProperty("reporter_id")
    private long reporterId;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("building_id")
    private long buildingId;
    @JsonProperty("data_address")
    private String detailAddress;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("reported_at")
    private LocalDateTime reportedAt;
    private String nickname;
}
