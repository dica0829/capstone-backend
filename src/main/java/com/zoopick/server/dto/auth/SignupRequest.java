package com.zoopick.server.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest{
    @NotBlank
    @Email
    private String schoolEmail;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    private String department;
    private String grade;
}