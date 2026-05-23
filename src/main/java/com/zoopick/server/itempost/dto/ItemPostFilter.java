package com.zoopick.server.itempost.dto;

import com.zoopick.server.item.entity.ItemCategory;
import com.zoopick.server.item.entity.ItemColor;
import com.zoopick.server.item.entity.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemPostFilter {
    private ItemStatus status;
    private ItemCategory category;
    private ItemColor color;
}
