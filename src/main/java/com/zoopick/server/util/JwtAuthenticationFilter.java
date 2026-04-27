package com.zoopick.server.util;

import com.zoopick.server.entity.User;
import com.zoopick.server.repository.UserRepository;
import com.zoopick.server.service.TokenValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@NullMarked
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final TokenValidationService tokenValidationService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = parseAccessToken(request);

        if (tokenValidationService.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findBySchoolEmail(email).orElseThrow();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, user.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String parseAccessToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return Objects.requireNonNullElse(token, "")
                .replace("Bearer ", "");
    }
}