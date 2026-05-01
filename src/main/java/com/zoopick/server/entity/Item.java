package com.zoopick.server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; //제목 추가

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false)
    private String category;

    private String color;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_name")
    private String locationName; // 위치명 추가

    private String imageUrl;

    private Double latitude;
    private Double longitude;

    private OffsetDateTime reportedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter; // 신고자 ID 추가

    @Builder.Default
    private String status = "REPORTED";
}