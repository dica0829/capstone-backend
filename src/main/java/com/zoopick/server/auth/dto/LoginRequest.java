package com.zoopick.server.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @Email
    @NotBlank
    private String schoolEmail;
    @NotBlank
    private String password;
}