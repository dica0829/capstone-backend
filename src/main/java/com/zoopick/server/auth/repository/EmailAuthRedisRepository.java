package com.zoopick.server.auth.repository;

import com.zoopick.server.auth.entity.EmailAuth;
import com.zoopick.server.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@NullMarked
public class EmailAuthRedisRepository {
    private static final long EMAIL_AUTH_TTL_MINUTES = 33L;
    private static final String EMAIL_AUTH_KEY_PREFIX = "emailAuth:";

    private final RedisTemplate<String, EmailAuth> emailAuthRedisTemplate;

    public EmailAuth getOrThrow(String email) {
        EmailAuth emailAuth = emailAuthRedisTemplate.opsForValue().get(emailAuthKey(email));
        if (emailAuth == null)
            throw new BadRequestException("인증 요청 기록이 없습니다.", email + " did not request certification yet.");
        return emailAuth;
    }

    public void save(EmailAuth emailAuth) {
        emailAuthRedisTemplate.opsForValue().set(
                emailAuthKey(emailAuth.getEmail()),
                emailAuth,
                EMAIL_AUTH_TTL_MINUTES,
                TimeUnit.MINUTES
        );
    }

    public void delete(String email) {
        emailAuthRedisTemplate.delete(emailAuthKey(email));
    }

    private static String emailAuthKey(String email) {
        return EMAIL_AUTH_KEY_PREFIX + email;
    }
}
