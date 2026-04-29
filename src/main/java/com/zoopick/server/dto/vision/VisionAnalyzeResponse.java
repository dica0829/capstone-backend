package com.zoopick.server.dto.vision;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VisionAnalyzeResponse {
    private String category;
    private String color;
    private List<Float> embedding;
}
