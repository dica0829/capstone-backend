package com.zoopick.server.controller;

import com.zoopick.server.dto.vision.VisionAnalyzeRequest;
import com.zoopick.server.dto.vision.VisionAnalyzeResponse;
import com.zoopick.server.service.VisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vision")
@RequiredArgsConstructor
public class VisionController {
    private final VisionService visionService;

    @PostMapping("/analyze")
    public VisionAnalyzeResponse analyzeImage(@RequestBody VisionAnalyzeRequest request) {
        return visionService.analyzeImage(request.getImageUrl());
    }
}
