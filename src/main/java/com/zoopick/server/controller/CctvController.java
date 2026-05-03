package com.zoopick.server.controller;

import com.zoopick.server.dto.cctv.CctvEnqueueResponse;
import com.zoopick.server.service.CctvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cctv")
@RequiredArgsConstructor
public class CctvController {
    private final CctvService cctvService;

    @PostMapping("/enqueue/{videoId}")
    public ResponseEntity<CctvEnqueueResponse> enqueueVideo(@PathVariable Long videoId) {
        CctvEnqueueResponse response = cctvService.enqueueVideo(videoId);
        return ResponseEntity.accepted().body(response);
    }
}
