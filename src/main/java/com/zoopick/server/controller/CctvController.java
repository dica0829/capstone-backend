package com.zoopick.server.controller;

import com.zoopick.server.dto.cctv.CctvEnqueueResponse;
import com.zoopick.server.dto.cctv.CctvVideoCreateRequest;
import com.zoopick.server.dto.cctv.CctvVideoCreateResponse;
import com.zoopick.server.service.CctvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cctv")
@RequiredArgsConstructor
public class CctvController {
    private final CctvService cctvService;

    @PostMapping("/videos")
    public ResponseEntity<CctvVideoCreateResponse> createVideoAndEnqueue(@Valid @RequestBody CctvVideoCreateRequest request) {
        CctvVideoCreateResponse response = cctvService.createVideoAndEnqueue(request);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/enqueue/{videoId}")
    public ResponseEntity<CctvEnqueueResponse> enqueueVideo(@PathVariable Long videoId) {
        CctvEnqueueResponse response = cctvService.enqueueVideo(videoId);
        return ResponseEntity.accepted().body(response);
    }
}
