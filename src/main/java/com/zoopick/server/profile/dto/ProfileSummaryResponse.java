package com.zoopick.server.profile.dto;

public record ProfileSummaryResponse(
        long userId,
        String nickname,
        String department,
        long postCount,
        long chatRoomCount,
        long unreadNotificationCount) {
}
