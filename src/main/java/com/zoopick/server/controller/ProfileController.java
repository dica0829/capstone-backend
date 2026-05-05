package com.zoopick.server.controller;

import com.zoopick.server.dto.profile.ProfileSummaryResponse;
import com.zoopick.server.dto.profile.ProfileUpdateRequest;
import com.zoopick.server.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile API", description = "사용자 프로필 관련 API")
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "내 프로필 요약 조회", description = "현재 로그인한 사용자의 프로필 기본 정보와 활동 요약(게시글 수, 채팅방 수, 안 읽은 알림 수)을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공")
    })
    @GetMapping("/me")
    public ResponseEntity<ProfileSummaryResponse> getMyProfile(Authentication authentication) {
        // JWT 토큰에서 추출된 email(또는 username)을 사용
        String email = authentication.getName();
        ProfileSummaryResponse response = profileService.getProfileSummary(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 프로필 정보 수정", description = "현재 로그인한 사용자의 닉네임과 학과 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 존재하는 닉네임인 경우)")
    })
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            Authentication authentication,
            @RequestBody ProfileUpdateRequest request) {

        try {
            String email = authentication.getName();
            profileService.updateProfile(email, request);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            // 닉네임 중복 시 400 Bad Request 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}