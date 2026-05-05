package com.zoopick.server.dto.profile;

public record ProfileUpdateRequest(
        String nickname,
        String department
) {
}
