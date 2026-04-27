package com.zoopick.server.service;

import com.zoopick.server.dto.auth.CheckCertificationRequest;
import com.zoopick.server.dto.auth.EmailCertificationRequest;
import com.zoopick.server.entity.EmailAuth;
import com.zoopick.server.entity.User;
import com.zoopick.server.repository.EmailAuthRepository;
import com.zoopick.server.repository.UserRepository;
import com.zoopick.server.util.EmailProvider;
import com.zoopick.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    // Expiration duration in minutes
    private static final int EXPIRATION_DURATION = 3;

    private final UserRepository userRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProvider emailProvider;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    private static LocalDateTime createNewExpireTime() {
        return LocalDateTime.now().plusHours(EXPIRATION_DURATION);
    }

    // 1. 회원가입
    public User signup(String email, String password, String nickname, String department, String grade) {
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
                .role("ROLE_STUDENT")
                .department(department)
                .grade(grade)
                .build();

        User savedUser = userRepository.save(user);

        // 가입 성공 후 인증 데이터 삭제
        emailAuthRepository.delete(emailAuth);

        return savedUser;
    }

    // 닉네임 중복 체크
    public boolean checkNickname(String nickname) {
        // Optional이 비어있으면(isEmpty) true(사용 가능)를 반환합니다.
        return userRepository.findByNickname(nickname).isEmpty();
    }

    // 2. 로그인
    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findBySchoolEmail(email);

        if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            return optionalUser.get();
        }
        return null;
    }

    public String extendAccessToken(String originalToken) {
        if (jwtUtil.validateToken(originalToken) && !redisService.hasKeyBlackList(originalToken)) {
            String email = jwtUtil.extractEmail(originalToken);
            userRepository.findBySchoolEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            return jwtUtil.generateToken(email);
        }
        throw new IllegalStateException("토큰이 유효하지 않습니다.");
    }

    // 3. 이메일 인증 발송
    @Transactional
    public void sendCertificationEmail(EmailCertificationRequest request) {
        String email = request.getEmail();

        if (userRepository.findBySchoolEmail(email).isPresent())
            throw new RuntimeException("이미 가입된 이메일입니다.");

        String certificationNumber = getCertificationNumber();

        EmailAuth emailAuth = new EmailAuth(email, certificationNumber, createNewExpireTime(), false);
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
        if (emailAuth.isExpired()) {
            emailAuthRepository.delete(emailAuth);
            throw new IllegalStateException("이메일 인증이 만료되었습니다.");
        }
        emailAuth.setVerified(true);
        emailAuthRepository.save(emailAuth);
    }

    // 6자리 난수 생성
    private String getCertificationNumber() {
        StringBuilder certificationNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            certificationNumber.append((int) (Math.random() * 10));
        }
        return certificationNumber.toString();
    }

    // 5. 로그아웃 로직
    public void logout(String accessToken) {
        // 1. 토큰 검증
        if (!jwtUtil.validateToken(accessToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 4. Access Token 블랙리스트 등록 (순수 토큰값만 저장됨)
        long expiration = jwtUtil.getExpiration(accessToken);
        if (expiration > 0) {
            redisService.setBlackList(accessToken, expiration);
        }
    }
}