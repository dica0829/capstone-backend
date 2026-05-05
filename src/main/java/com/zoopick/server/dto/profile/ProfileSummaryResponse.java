package com.zoopick.server.dto.profile;

public record ProfileSummaryResponse(
        String nickname,
        String department,
        long postCount,
        long chatRoomCount,
        long unreadNotificationCount) {
}
