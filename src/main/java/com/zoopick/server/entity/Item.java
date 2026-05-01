package com.zoopick.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "item_type")
    private ItemType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "item_status")
    @Builder.Default
    private ItemStatus status = ItemStatus.REPORTED;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "item_category")
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "item_color")
    private ItemColor color;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(512)")
    private float[] embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_building_id")
    private Building reportedBuilding;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "theft_suspected_at")
    private LocalDateTime theftSuspectedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
