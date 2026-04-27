package com.zoopick.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@NullMarked
public class JwtUtil {
    // 토큰 만료 시간 1일
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토근의 만료 시간, 인증 확인후 결과 반환한다.<br/>
     * 토큰의 무효화 검사가 필요할 경우 {@link com.zoopick.server.service.TokenValidationService#isValidToken(String)} 사용
     *
     * @param token 검사할 토큰
     * @return True 일 시 유효한 토큰
     * @see com.zoopick.server.service.TokenValidationService#isValidToken(String)
     */
    public boolean validateToken(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * token의 만료까지 남은 시간을 밀리초 단위로 반환한다.
     *
     * @param token 토큰
     * @return 만료까지 남은 시간 (ms)
     */
    public long getRemainingExpirationTime(String token) {
        Date expiration = getClaims(token).getExpiration();
        return (expiration.getTime() - System.currentTimeMillis());
    }
}