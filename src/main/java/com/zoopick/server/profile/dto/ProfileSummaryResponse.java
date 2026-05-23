package com.zoopick.server.profile.dto;

public record ProfileSummaryResponse(
        String nickname,
        String department,
        long postCount,
        long chatRoomCount,
        long unreadNotificationCount) {
}
