package com.zoopick.server.dto.vision;

import com.zoopick.server.entity.ItemCategory;
import com.zoopick.server.entity.ItemColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VisionAnalyzeResponse {
    private ItemCategory category;
    private ItemColor color;
    private List<Float> embedding;
}
