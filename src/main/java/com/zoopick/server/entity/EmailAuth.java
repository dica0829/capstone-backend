package com.zoopick.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuth {
    @Id
    private String email;

    private String certificationCode;

    private LocalDateTime expireTime;

    private boolean isVerified = false;

    public boolean isCertificationCodeExpired() {
        return expireTime.isBefore(LocalDateTime.now());
    }

    public boolean isSignupExpired() {
        return expireTime.plusMinutes(30).isBefore(LocalDateTime.now());
    }
}