package com.zoopick.server.itemmatch.event;

import java.util.List;

public record CreateMatchEvent(List<Entry> entries) {
    public record Entry(long matchId, float score, long lostItemId, long reporterUserId, String itemPostTitle,
                        String foundLocationName) {
    }
}
