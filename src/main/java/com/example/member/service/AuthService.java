package com.example.member.service;

import com.example.member.dto.CheckCertificationRequest;
import com.example.member.dto.EmailcertificationRequest;
import com.example.member.entity.EmailAuth;
import com.example.member.entity.User;
import com.example.member.repository.EmailAuthRepository;
import com.example.member.repository.UserRepository;
import com.example.member.util.EmailProvider;
import com.example.member.util.JwtUtil;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProvider emailProvider;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    // 1. 회원가입
    public User signup(String email, String password, String nickname) {
        EmailAuth emailAuth = emailAuthRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("이메일 인증을 진행해주세요."));

        if (!emailAuth.isVerified()) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new RuntimeException("이미 사용중인 닉네임입니다.");
        }

        User user = User.builder()
                .schoolEmail(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();

        User savedUser = userRepository.save(user);

        // 가입 성공 후 인증 데이터 삭제
        emailAuthRepository.delete(emailAuth);

        return savedUser;
    }

    // 2. 로그인
    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findBySchoolEmail(email);

        if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            return optionalUser.get();
        }
        return null;
    }

    // 3. 이메일 인증 발송
    @Transactional
    public void sendCertificationEmail(EmailcertificationRequest request) {
        String email = request.getEmail();

        if (userRepository.findBySchoolEmail(email).isPresent()) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        String certificationNumber = getCertificationNumber();

        EmailAuth emailAuth = new EmailAuth(email, certificationNumber, false);
        emailAuthRepository.save(emailAuth);

        emailProvider.senderCertificationMail(email, certificationNumber);
    }

    // 4. 인증번호 검증 로직
    @Transactional
    public void verifyCertificationCode(CheckCertificationRequest request) {
        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmail())
                .orElseThrow(() -> new RuntimeException("인증 요청 기록이 없습니다."));

        if (!emailAuth.getCertificationNumber().equals(request.getCertificationNumber())) {
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }
        emailAuth.setVerified(true);
        emailAuthRepository.save(emailAuth);
    }

    //6자리 난수 생성
    private String getCertificationNumber() {
        StringBuilder certificationNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            certificationNumber.append((int) (Math.random() * 10));
        }
        return certificationNumber.toString();
    }


    // 5. 로그아웃 로직
    // AuthService.java의 로그아웃 로직 수정
    public void logout(String authHeader) {
        String accessToken = authHeader;

        // 0. Bearer 접두사가 있다면 제거
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        // 1. 토큰 검증
        if (!jwtUtil.validateToken(accessToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 2. 토큰에서 이메일 추출
        String email = jwtUtil.extractEmail(accessToken);

        // 3. Refresh Token 삭제
        redisService.deleteRefreshToken(email);

        // 4. Access Token 블랙리스트 등록 (순수 토큰값만 저장됨)
        long expiration = jwtUtil.getExpiration(accessToken);
        if (expiration > 0) {
            redisService.setBlackList(accessToken, expiration);
        }
    }

}