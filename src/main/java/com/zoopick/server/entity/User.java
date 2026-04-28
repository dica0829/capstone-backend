package com.zoopick.server.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "school_email", nullable = false, unique = true)
    private String schoolEmail;

    @Column
    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(name = "fcm_token")
    @Nullable
    private String fcmToken;

    @Column
    private String role;

    @Column
    private String department;

    @Column
    private String grade;

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }
}