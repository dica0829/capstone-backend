package com.zoopick.server.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResult {
    @JsonProperty("user_id")
    private long userId;
    private String message;
    @JsonProperty("access_token")
    private String accessToken;
}
