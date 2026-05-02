package com.zoopick.server.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Entity
@Table(name = "users", schema = "zoopick")
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

    @Column(nullable = false)
    private String password;

    @Column(length = 50, nullable = false, unique = true)
    private String nickname;

    @Column(name = "fcm_token", length = 512)
    @Nullable
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "user_role")
    private Role role;

    @Column(length = 50, nullable = false)
    private String department;

    @Column(length = 20, nullable = false)
    private String grade;

    public List<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
