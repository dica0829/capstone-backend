package com.zoopick.server.auth.controller;

import com.zoopick.server.auth.dto.*;
import com.zoopick.server.auth.service.AuthService;
import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Auth API", description = "학생 이메일 인증 및 회원가입/로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@NullMarked
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "서버에 이메일 인증이 완료된 상태인지 확인 후 가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 미인증 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<SignupResult>> signup(@RequestBody @Valid SignupRequest request) {
        SignupResult signupResult = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(signupResult));
    }

    @Operation(summary = "닉네임 중복 체크", description = "가입 전 닉네임 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-nickname")
    public ResponseEntity<CommonResponse<NicknameCheckResult>> checkNickname(@RequestParam String nickname) {
        NicknameCheckResult nicknameCheckResult = authService.checkNickname(nickname);
        return ResponseEntity.ok(CommonResponse.success(nicknameCheckResult));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행하고 JWT 토큰을 반환받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResult>> login(@RequestBody @Valid LoginRequest request) {
        LoginResult loginResult = authService.login(request);
        return ResponseEntity.ok(CommonResponse.success(loginResult));
    }

    @Operation(summary = "액세스 토큰 확인", description = "토큰이 유효하면 연장된 토큰을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "실패")
    })
    @PostMapping("/validate")
    public ResponseEntity<CommonResponse<String>> validateAccessToken(HttpServletRequest request) {
        String accessToken = request.getAttribute("accessToken").toString();
        String newToken = authService.validateAccessToken(accessToken);
        return ResponseEntity.ok(CommonResponse.success(newToken));
    }

    @Operation(summary = "이메일 인증 번호 발송", description = "가입하려는 이메일로 인증 코드를 발송합니다.")
    @PostMapping("/certification")
    public ResponseEntity<CommonResponse<String>> sendCertificationEmail(
            @RequestBody @Valid EmailCertificationRequest request
    ) throws MessagingException, IOException {
        authService.sendCertificationEmail(request);
        return ResponseEntity.ok(CommonResponse.success("인증 코드가 발송되었습니다."));
    }

    @Operation(summary = "인증 번호 확인 (검증)", description = "이메일로 발송된 6자리 인증 번호가 맞는지 검증합니다.")
    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<String>> verifyCertificationCode(@RequestBody @Valid CheckCertificationRequest request) {
        authService.verifyCertificationCode(request);
        return ResponseEntity.ok(CommonResponse.success("인증에 성공하였습니다."));
    }

    @Operation(summary = "로그아웃", description = "Access Token을 블랙리스트에 등록하여 로그아웃을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(
            @AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request
    ) {
        String accessToken = request.getAttribute("accessToken").toString();
        authService.logout(principal.id(), accessToken);
        return ResponseEntity.ok(CommonResponse.success("로그아웃이 완료되었습니다."));
    }
}