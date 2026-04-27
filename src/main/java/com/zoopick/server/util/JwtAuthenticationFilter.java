package com.zoopick.server.util;

import com.zoopick.server.entity.User;
import com.zoopick.server.repository.UserRepository;
import com.zoopick.server.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = parseAccessToken(request);

        // 1. 토큰이 존재하고, 유효기간이 지나지 않았는지 검증
        if (jwtUtil.validateToken(token)) {
            // 2. Redis에 저장된 블랙리스트(로그아웃 처리된 토큰)에 없는지 확인
            if (!redisService.hasKeyBlackList(token)) {
                String email = jwtUtil.extractEmail(token);
                User user = userRepository.findBySchoolEmail(email).orElseThrow();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, user.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.info("로그아웃 처리된 토큰으로 접근을 시도했습니다.");
            }
        }

        filterChain.doFilter(request, response);
    }

    private String parseAccessToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return Objects.requireNonNullElse(token, "")
                .replace("Bearer ", "");
    }
}