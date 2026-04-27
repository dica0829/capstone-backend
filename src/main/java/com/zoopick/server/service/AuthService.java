package com.zoopick.server.service;

import com.zoopick.server.dto.auth.CheckCertificationRequest;
import com.zoopick.server.dto.auth.EmailCertificationRequest;
import com.zoopick.server.dto.auth.LoginRequest;
import com.zoopick.server.dto.auth.SignupRequest;
import com.zoopick.server.entity.EmailAuth;
import com.zoopick.server.entity.User;
import com.zoopick.server.repository.EmailAuthRepository;
import com.zoopick.server.repository.UserRepository;
import com.zoopick.server.util.EmailProvider;
import com.zoopick.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@NullMarked
public class AuthService {
    // Expiration duration in minutes
    private static final int EMAIL_CERTIFICATION_EXPIRE_DURATION = 3;

    private final UserRepository userRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailProvider emailProvider;
    private final JwtUtil jwtUtil;
    private final TokenValidationService tokenValidationService;

    private static LocalDateTime createNewExpireTime() {
        return LocalDateTime.now().plusMinutes(EMAIL_CERTIFICATION_EXPIRE_DURATION);
    }

    public User signup(SignupRequest request) {
        EmailAuth emailAuth = emailAuthRepository.findById(request.getSchoolEmail())
                .orElseThrow(() -> new IllegalStateException("이메일 인증을 진행해주세요."));

        if (!emailAuth.isVerified()) {
            throw new IllegalStateException("이메일 인증이 완료되지 않았습니다.");
        }
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new IllegalStateException("이미 사용중인 닉네임입니다.");
        }

        User user = User.builder()
                .schoolEmail(request.getSchoolEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("ROLE_STUDENT")
                .department(request.getDepartment())
                .grade(request.getGrade())
                .build();

        User savedUser = userRepository.save(user);
        emailAuthRepository.delete(emailAuth);

        return savedUser;
    }

    public boolean checkNickname(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    public User login(LoginRequest request) {
        User user = userRepository.findBySchoolEmail(request.getSchoolEmail())
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword()))
            return user;
        throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
    }

    /**
     * originalToken이 유효한지 확인하고 새로운 토큰 반환
     *
     * @param originalToken 기존 토큰
     * @return 만료 시간이 갱신된 새로운 토큰
     */
    public String validateAccessToken(String originalToken) {
        if (tokenValidationService.isValidToken(originalToken)) {
            String email = jwtUtil.extractEmail(originalToken);
            userRepository.findBySchoolEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            tokenValidationService.invalidateToken(originalToken);
            return jwtUtil.generateToken(email);
        }
        throw new IllegalStateException("토큰이 유효하지 않습니다.");
    }

    @Transactional
    public void sendCertificationEmail(EmailCertificationRequest request) {
        String email = request.getEmail();

        if (userRepository.findBySchoolEmail(email).isPresent())
            throw new IllegalStateException("이미 가입된 이메일입니다.");

        String certificationNumber = generateCertificationNumber();

        EmailAuth emailAuth = new EmailAuth(email, certificationNumber, createNewExpireTime(), false);
        emailAuthRepository.save(emailAuth);

        emailProvider.senderCertificationMail(email, certificationNumber);
    }

    @Transactional
    public void verifyCertificationCode(CheckCertificationRequest request) {
        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("인증 요청 기록이 없습니다."));

        if (!emailAuth.getCertificationNumber().equals(request.getCertificationNumber())) {
            throw new IllegalStateException("인증번호가 일치하지 않습니다.");
        }
        if (emailAuth.isExpired()) {
            emailAuthRepository.delete(emailAuth);
            throw new IllegalStateException("이메일 인증이 만료되었습니다.");
        }
        emailAuth.setVerified(true);
        emailAuthRepository.save(emailAuth);
    }

    private String generateCertificationNumber() {
        StringBuilder certificationNumber = new StringBuilder();
        Random random = new Random();

        for (int count = 0; count < 6; count++) {
            int digit = random.nextInt(10);
            certificationNumber.append(digit);
        }
        return certificationNumber.toString();
    }

    public void logout(String accessToken) {
        if (!tokenValidationService.isValidToken(accessToken))
            throw new IllegalStateException("유효하지 않은 토큰입니다.");

        tokenValidationService.invalidateToken(accessToken);
    }
}