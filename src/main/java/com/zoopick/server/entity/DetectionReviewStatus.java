package com.zoopick.server.entity;

public enum DetectionReviewStatus {
    PENDING,        // 확인 대기 중
    CONFIRMED_SELF, // 본인 물건 확인
    REJECTED_SELF,  // 본인 물건 아님
    UNCERTAIN       // 잘 모르겠음
}
