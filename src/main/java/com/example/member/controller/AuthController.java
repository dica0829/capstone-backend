package com.example.member.controller;

import com.example.member.dto.CommonResponse;
import com.example.member.entity.User;
import com.example.member.service.AuthService;
import com.example.member.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/send-code")
    public ResponseEntity<CommonResponse<String>> sendCode(@RequestBody Map<String, String> data) {
        String email = data.get("school_email");
        authService.sendAuthCode(email);
        return ResponseEntity.ok(CommonResponse.success("인증번호가 메일로 발송되었습니다."));
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Map<String, Object>>> signup(@RequestBody Map<String, String> data) {
        try {
            User savedUser = authService.signup(
                    data.get("school_email"),
                    data.get("password"),
                    data.get("nickname"),
                    data.get("auth_code")
            );

            Map<String, Object> result = new HashMap<>();
            result.put("user_id", savedUser.getId());
            result.put("message", "회원가입 성공!");
            return ResponseEntity.status(201).body(CommonResponse.success(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(CommonResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Map<String, Object>>> login(@RequestBody Map<String, String> data) {
        String email = data.get("school_email");
        User user = authService.login(email, data.get("password"));

        if (user != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("token", jwtUtil.generateToken(email));
            result.put("nickname", user.getNickname());
            result.put("message", "로그인 성공");
            return ResponseEntity.ok(CommonResponse.success(result));
        }
        return ResponseEntity.status(401).body(CommonResponse.error("로그인 정보가 틀렸습니다."));
    }
}