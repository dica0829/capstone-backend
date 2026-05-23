package com.zoopick.server.profile.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfileUpdateRequest(

        @NotBlank
        String nickname,

        @NotBlank
        String department
) {
}
