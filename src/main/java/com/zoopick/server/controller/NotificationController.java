package com.zoopick.server.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.dto.fcm.FcmNotificationRequest;
import com.zoopick.server.dto.fcm.FcmTokenRegistrationRequest;
import com.zoopick.server.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification API", description = "FCM 토큰을 등록 및 알림 전송")
@RestController
@RequiredArgsConstructor
@NullMarked
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "FCM 토큰 등록", description = "클라이언트의 FCM 토큰을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/api/auth/device-token")
    public ResponseEntity<CommonResponse<String>> registerFcmToken(
            @RequestHeader(value = "Authorization", defaultValue = "") String accessToken,
            @RequestBody @Valid FcmTokenRegistrationRequest request
    ) {
        accessToken = accessToken.replace("Bearer ", "");
        notificationService.register(accessToken, request.getToken());
        return ResponseEntity.ok(CommonResponse.success("FCM 토큰이 등록되었습니다."));
    }

    @Operation(summary = "대상에게 알림 전송", description = "클라이언트로 알림을 보냅니다. (ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 전송 성공"),
            @ApiResponse(responseCode = "404", description = "닉네임을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "FirebaseMessaging 오류")
    })
    @PostMapping("/admin/send/{targetNickname}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<String>> sendNotification(
            @PathVariable("targetNickname") String targetNickname,
            @RequestBody @Valid FcmNotificationRequest request
    ) throws FirebaseMessagingException {
        String result = notificationService.send(targetNickname, request);
        return ResponseEntity.ok(CommonResponse.success(result));
    }
}
