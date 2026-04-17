package com.example.member.controller;

import com.example.member.dto.*;
import com.example.member.entity.User;
import com.example.member.service.AuthService;
import com.example.member.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth API", description = "학생 이메일 인증 및 회원가입/로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "1. 회원가입", description = "서버에 이메일 인증이 완료된 상태인지 확인 후 가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 미인증 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Map<String, Object>>> signup(@RequestBody @Valid SignupRequest request) {
        try {
            User savedUser = authService.signup(
                    request.getSchoolEmail(),
                    request.getPassword(),
                    request.getNickname()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("userId", savedUser.getId());
            result.put("message", "회원가입이 완료되었습니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "2. 로그인", description = "이메일과 비밀번호로 로그인을 진행하고 JWT 토큰을 반환받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 불일치")
    })
    @PostMapping("/login")
    // 💡 별도의 LoginRequest DTO 사용 권장 (nickname이 필요 없으므로)
    public ResponseEntity<CommonResponse<Map<String, Object>>> login(@RequestBody @Valid LoginRequest request) {
        try {
            User user = authService.login(request.getSchoolEmail(), request.getPassword());

            if (user != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("token", jwtUtil.generateToken(user.getSchoolEmail()));
                result.put("nickname", user.getNickname());
                result.put("message", "로그인 성공");

                return ResponseEntity.ok(CommonResponse.success(result));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error("로그인 정보가 틀렸습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "3. 이메일 인증 번호 발송", description = "가입하려는 이메일로 인증 코드를 발송합니다.")
    @PostMapping("/certification")
    public ResponseEntity<CommonResponse<String>> sendCertificationEmail(@RequestBody @Valid EmailcertificationRequest request) {
        try {
            authService.sendCertificationEmail(request);
            return ResponseEntity.ok(CommonResponse.success("인증 코드가 발송되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "4. 인증 번호 확인 (검증)", description = "이메일로 발송된 6자리 인증 번호가 맞는지 검증합니다.")
    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<String>> verifyCertificationCode(@RequestBody @Valid CheckCertificationRequest request) {
        try {
            authService.verifyCertificationCode(request);
            return ResponseEntity.ok(CommonResponse.success("인증에 성공하였습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "5. 로그아웃", description = "Access Token을 블랙리스트에 등록하여 로그아웃을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(@RequestHeader(value = "Authorization", required = false) String headerAuth) {
        try {
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                String token = headerAuth.substring(7);
                authService.logout(token);
                return ResponseEntity.ok(CommonResponse.success("로그아웃이 완료되었습니다."));
            }
            return ResponseEntity.badRequest().body(CommonResponse.error("토큰이 존재하지 않습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }
}