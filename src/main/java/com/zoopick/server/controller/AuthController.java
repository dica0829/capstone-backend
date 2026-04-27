package com.zoopick.server.controller;

import com.zoopick.server.dto.CommonResponse;
import com.zoopick.server.dto.auth.CheckCertificationRequest;
import com.zoopick.server.dto.auth.EmailCertificationRequest;
import com.zoopick.server.dto.auth.LoginRequest;
import com.zoopick.server.dto.auth.SignupRequest;
import com.zoopick.server.entity.User;
import com.zoopick.server.service.AuthService;
import com.zoopick.server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "회원가입", description = "서버에 이메일 인증이 완료된 상태인지 확인 후 가입을 처리합니다.")
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
                    request.getNickname(),
                    request.getDepartment(),
                    request.getGrade()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("userId", savedUser.getId());
            result.put("message", "회원가입이 완료되었습니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "닉네임 중복 체크", description = "가입 전 닉네임 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-nickname")
    public ResponseEntity<CommonResponse<Map<String, Object>>> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = authService.checkNickname(nickname);
        Map<String, Object> result = new HashMap<>();
        result.put("isAvailable", isAvailable);
        result.put("message", "사용가능한 닉네임 입니다.");

        if (isAvailable) {
            result.put("message", "사용 가능한 닉네임입니다.");
        } else {
            result.put("message", "이미 사용 중인 닉네임입니다.");
        }
        return ResponseEntity.ok(CommonResponse.success(result));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행하고 JWT 토큰을 반환받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Map<String, Object>>> login(@RequestBody @Valid LoginRequest request) {
        try {
            User user = authService.login(request.getSchoolEmail(), request.getPassword());

            if (user != null) {
                Map<String, Object> result = new HashMap<>();
                //
                result.put("accessToken", jwtUtil.generateToken(user.getSchoolEmail()));

                result.put("nickname", user.getNickname());
                result.put("department", user.getDepartment());
                result.put("grade", user.getGrade());
                result.put("message", "로그인 성공");

                return ResponseEntity.ok(CommonResponse.success(result));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error("로그인 정보가 틀렸습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "세션 토큰 확인", description = "세션이 유효하면 연장된 토큰을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "실패")
    })
    @PostMapping("/validate")
    public ResponseEntity<CommonResponse<String>> validateAccessToken(@RequestHeader(value = "Authorization", defaultValue = "") String accessToken) {
        try {
            accessToken = accessToken.replace("Bearer ", "");
            String newToken = authService.extendAccessToken(accessToken);
            return ResponseEntity.ok(CommonResponse.success(newToken));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(exception.getMessage()));
        }
    }

    @Operation(summary = "이메일 인증 번호 발송", description = "가입하려는 이메일로 인증 코드를 발송합니다.")
    @PostMapping("/certification")
    public ResponseEntity<CommonResponse<String>> sendCertificationEmail(@RequestBody @Valid EmailCertificationRequest request) {
        try {
            authService.sendCertificationEmail(request);
            return ResponseEntity.ok(CommonResponse.success("인증 코드가 발송되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "인증 번호 확인 (검증)", description = "이메일로 발송된 6자리 인증 번호가 맞는지 검증합니다.")
    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<String>> verifyCertificationCode(@RequestBody @Valid CheckCertificationRequest request) {
        try {
            authService.verifyCertificationCode(request);
            return ResponseEntity.ok(CommonResponse.success("인증에 성공하였습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "로그아웃", description = "Access Token을 블랙리스트에 등록하여 로그아웃을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(@RequestHeader(value = "Authorization", defaultValue = "") String accessToken) {
        try {
            accessToken = accessToken.replace("Bearer ", "");
            authService.logout(accessToken);
            return ResponseEntity.ok(CommonResponse.success("로그아웃이 완료되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }
}