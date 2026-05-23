package com.zoopick.server.vision.dto;

import com.zoopick.server.item.entity.ItemCategory;
import com.zoopick.server.item.entity.ItemColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VisionAnalyzeResponse {
    private ItemCategory category;
    private ItemColor color;
    private float[] embedding;
}
