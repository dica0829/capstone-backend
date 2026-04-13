package com.example.member.service;

import com.example.member.entity.User;
import com.example.member.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    // 이메일별 인증코드를 안전하게 보관 (메모리 방식)
    private final Map<String, String> authCodeMap = new ConcurrentHashMap<>();

    public void sendAuthCode(String email) {
        String authCode = String.valueOf((int)(Math.random() * 899999) + 100000);
        authCodeMap.put(email, authCode);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            // 💡 첫 번째 인자는 실제 계정 주소, 두 번째 인자는 표시될 '이름'입니다.
            helper.setFrom("", "명지대 분실물 센터");
            helper.setSubject("[명지대 분실물 센터] 회원가입 인증번호입니다.");
            helper.setText("인증번호는 <b>" + authCode + "</b> 입니다.", true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("메일 발송 중 오류가 발생했습니다.");
        }
    }


    @Transactional
    public User signup(String email, String password, String nickname, String inputCode) {

        // 1. 인증코드 일치 여부 확인
        String savedCode = authCodeMap.get(email);
        if (savedCode == null || !savedCode.equals(inputCode)) {
            throw new RuntimeException("인증번호가 일치하지 만료되었거나 일치하지 않습니다.");
        }

        // 2. 계정 및 닉네임 중복 확인
        if (userRepository.findBySchoolEmail(email).isPresent()) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 암호화 및 저장
        User user = User.builder()
                .schoolEmail(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();

        authCodeMap.remove(email); // 가입 성공 시 코드 파기
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findBySchoolEmail(email);
        if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            return optionalUser.get();
        }
        return null;
    }
}