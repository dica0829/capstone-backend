package com.example.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // Refresh Token 삭제
    public void deleteRefreshToken(String email) {
        redisTemplate.delete("RT:" + email);
    }

    // Access Token 블랙리스트 추가 (로그아웃용)
    public void setBlackList(String accessToken, long expirationTime) {
        redisTemplate.opsForValue().set(
                accessToken,
                "logout",
                expirationTime,
                TimeUnit.MILLISECONDS
        );
    }

    // 블랙리스트에 토큰이 있는지 확인
    public boolean hasKeyBlackList(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));
    }
}