package com.example.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest{
    private String schoolEmail;
    private String password;
    private String nickname;
    private String authCode;
}