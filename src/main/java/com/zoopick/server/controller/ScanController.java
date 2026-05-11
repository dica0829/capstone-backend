package com.zoopick.server.controller;

import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.dto.scan.ScanOwnerResult;
import com.zoopick.server.service.ProfileService;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scan")
@NullMarked
public class ScanController {
    private final ProfileService profileService;

    public ScanController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/owner/{userId}")
    public ResponseEntity<CommonResponse<ScanOwnerResult>> scanOwner(@PathVariable long userId) {
        String nickname = profileService.findNickname(userId);
        return ResponseEntity.ok(CommonResponse.success(new ScanOwnerResult(userId, nickname)));
    }
}
