package com.zoopick.server.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuth {
    private String email;

    private String certificationCode;

    private LocalDateTime expireTime;

    @JsonProperty("verified")
    @JsonAlias("isVerified")
    private Boolean verified = false;

    public boolean isVerified() {
        return Boolean.TRUE.equals(verified);
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @JsonIgnore
    public boolean isCertificationCodeExpired() {
        return expireTime.isBefore(LocalDateTime.now());
    }

    @JsonIgnore
    public boolean isSignupExpired() {
        return expireTime.plusMinutes(30).isBefore(LocalDateTime.now());
    }
}
