package com.zoopick.server.service;

import com.zoopick.server.exception.AccessTokenException;
import com.zoopick.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 토큰의 유효성 검사와 무효화 관리
 *
 * @see JwtUtil
 */
@Service
@RequiredArgsConstructor
@NullMarked
public class TokenValidationService {
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate template;

    /**
     * 토큰을 무효화한다. 토큰의 만료 시간이 지났다면 무시한다.
     *
     * @param accessToken 무효화할 투콘
     */
    public void invalidateToken(String accessToken) throws AccessTokenException {
        long expirationTime = jwtUtil.getRemainingExpirationTime(accessToken);
        if (expirationTime > 0) {
            template.opsForValue().set(
                    accessToken, "logout",
                    expirationTime, TimeUnit.MILLISECONDS
            );
        }
    }

    /**
     * 토근의 만료 시간, 인증 확인, 무효화 여부 확인 후 결과 반환한다.<br/>
     * {@link JwtUtil#validateToken(String)}은 토근의 만료 시간, 인증 확인만 수행한다.
     *
     * @param accessToken 확인할 토큰
     * @return True 일 시 유효한 토큰
     * @throws AccessTokenException 토큰이 유효하지 않을 때
     * @see JwtUtil#validateToken(String)
     * @see #validateToken(String)
     */
    public boolean validateTokenOrThrow(String accessToken) {
        return !template.hasKey(accessToken) && jwtUtil.validateToken(accessToken);
    }

    /**
     * 예외 없이 토큰의 요효성 확인
     *
     * @param accessToken 확인할 토큰
     * @return True 일 시 유효한 토큰
     * @see #validateTokenOrThrow(String)
     */
    public boolean validateToken(String accessToken) {
        try {
            return validateTokenOrThrow(accessToken);
        } catch (AccessTokenException exception) {
            return false;
        }
    }

    public String findInvalidCause(String accessToken) {
        if (template.hasKey(accessToken))
            return "invalidated token";
        if (jwtUtil.validateToken(accessToken))
            return "valid token";
        return "expired token";
    }
}