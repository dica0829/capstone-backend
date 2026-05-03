package com.zoopick.server.controller;

import com.zoopick.server.dto.cctv.*;
import com.zoopick.server.service.CctvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/internal/cctv")
@RequiredArgsConstructor
public class CctvInternalController {
    private final CctvService cctvService;

    @PostMapping("/progress")
    public ResponseEntity<?> updateProgress(@RequestBody CctvProgressCallback callback) {
        cctvService.updateProgress(callback);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/detection")
    public ResponseEntity<?> registerDetection(@RequestBody CctvDetectionCallback callback) {
        cctvService.registerDetection(callback);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/completed")
    public ResponseEntity<?> completeAnalysis(@RequestBody CctvCompletedCallback callback) {
        cctvService.completeAnalysis(callback);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/failed")
    public ResponseEntity<?> failAnalysis(@RequestBody CctvFailedCallback callback) {
        cctvService.failAnalysis(callback);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
