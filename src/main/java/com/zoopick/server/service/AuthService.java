package com.zoopick.server.service;

import com.zoopick.server.dto.auth.CheckCertificationRequest;
import com.zoopick.server.dto.auth.EmailCertificationRequest;
import com.zoopick.server.dto.auth.LoginRequest;
import com.zoopick.server.dto.auth.SignupRequest;
import com.zoopick.server.entity.EmailAuth;
import com.zoopick.server.entity.User;
import com.zoopick.server.exception.AccessTokenException;
import com.zoopick.server.exception.BadRequestException;
import com.zoopick.server.exception.DataNotFoundException;
import com.zoopick.server.repository.EmailAuthRepository;
import com.zoopick.server.repository.UserRepository;
import com.zoopick.server.util.EmailProvider;
import com.zoopick.server.util.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
                .orElseThrow(() -> new BadRequestException("이메일 인증을 진행해주세요.", request.getSchoolEmail() + " is not certificated."));
        if (!emailAuth.isVerified())
            throw new BadRequestException("이메일 인증이 완료되지 않았습니다.", request.getSchoolEmail() + " is not verified.");
        if (emailAuth.isSignupExpired()) {
            emailAuthRepository.delete(emailAuth);
            throw new BadRequestException("인증 코드가 만료되었습니다.", request.getSchoolEmail() + " is signup expired.");
        }
        if (userRepository.findByNickname(request.getNickname()).isPresent())
            throw new BadRequestException("이미 사용중인 닉네임입니다.", request.getNickname() + " is already in use.");

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
                .orElseThrow(() -> new BadRequestException("로그인에 실패했습니다.", request.getSchoolEmail() + " is not in UserRepository"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword()))
            return user;
        throw new BadRequestException("로그인에 실패했습니다.", request.getSchoolEmail() + " failed password.");
    }

    /**
     * originalToken이 유효한지 확인하고 새로운 토큰 반환
     *
     * @param originalToken 기존 토큰
     * @return 만료 시간이 갱신된 새로운 토큰
     */
    public String validateAccessToken(String originalToken) {
        if (tokenValidationService.validateTokenOrThrow(originalToken)) {
            String email = jwtUtil.extractEmail(originalToken);
            userRepository.findBySchoolEmail(email)
                    .orElseThrow(() -> DataNotFoundException.from("사용자", email));
            tokenValidationService.invalidateToken(originalToken);
            return jwtUtil.generateToken(email);
        }
        throw new AccessTokenException(originalToken + " is expired or invalidated.");
    }

    @Transactional
    public void sendCertificationEmail(EmailCertificationRequest request)
            throws MessagingException, IOException {
        String email = request.getEmail();

        if (userRepository.findBySchoolEmail(email).isPresent())
            throw new BadRequestException("이미 가입된 이메일입니다.", email + " is already in use.");

        String certificationNumber = generateCertificationNumber();

        EmailAuth emailAuth = new EmailAuth(email, certificationNumber, createNewExpireTime(), false);
        emailAuthRepository.save(emailAuth);

        emailProvider.senderCertificationMail(email, certificationNumber);
    }

    @Transactional
    public void verifyCertificationCode(CheckCertificationRequest request) {
        EmailAuth emailAuth = emailAuthRepository.findById(request.getEmail())
                .orElseThrow(() -> new BadRequestException("인증 요청 기록이 없습니다.", request.getEmail() + " did not request certification yet."));

        if (!emailAuth.getCertificationCode().equals(request.getCertificationNumber())) {
            throw new BadRequestException(
                    "인증번호가 일치하지 않습니다.",
                    "%s's Certification code does not match.".formatted(emailAuth.getEmail())
            );
        }
        if (emailAuth.isCertificationCodeExpired()) {
            emailAuthRepository.delete(emailAuth);
            throw new BadRequestException("이메일 인증이 만료되었습니다.", request.getEmail() + " has expired for certification.");
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
        if (!tokenValidationService.validateTokenOrThrow(accessToken))
            throw new AccessTokenException(accessToken + " is expired or invalidated.");

        tokenValidationService.invalidateToken(accessToken);
    }
}