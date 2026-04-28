package com.zoopick.server.service;

import com.zoopick.server.entity.User;
import com.zoopick.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 실제로 호출하지 않지면 spring security 에서 요구
 */
@Service
@RequiredArgsConstructor
@NullMarked
public class ZoopickUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findBySchoolEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " is not found."));
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();
    }
}
