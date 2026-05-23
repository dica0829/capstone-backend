package com.zoopick.server.cctv.event;

import java.util.List;

public record CreateCctvMatchEvent(List<Entry> entries) {
    public record Entry(long matchId, float score, long lostItemId, long reporterUserId, String itemPostTitle,
                        String roomName) {
    }
}
