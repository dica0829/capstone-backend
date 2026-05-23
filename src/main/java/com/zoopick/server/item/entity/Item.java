package com.zoopick.server.item.entity;

import com.zoopick.server.annotation.ServiceOnly;
import com.zoopick.server.auth.entity.User;
import com.zoopick.server.item.service.ItemService;
import com.zoopick.server.metadata.entity.Building;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "items", schema = "zoopick")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "item_type")
    private ItemType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "item_status")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private ItemStatus status = ItemStatus.REPORTED;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "item_category")
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "item_color")
    private ItemColor color;

    @Column(columnDefinition = "vector(512)")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 512)
    private float[] embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_building_id")
    private Building reportedBuilding;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "theft_suspected_at")
    private LocalDateTime theftSuspectedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * 아이템 상태만 변경한다.
     * 채팅방 종료, 이벤트 발행 등 부수 효과가 필요한 상태 변경은
     * 이 메소드를 직접 호출하지 말고 {@link ItemService#changeItemStatus(long, ItemStatus)}를 사용한다.
     *
     * @param status 바꿀 상태
     */
    @ServiceOnly(ItemService.class)
    public void changeStatus(ItemStatus status) {
        this.status = status;
        if (status == ItemStatus.RETURNED)
            this.returnedAt = LocalDateTime.now();
    }

    public String getDisplayName() {
        if (category == null)
            return "";
        return category.getDisplayName();
    }

    public void theftSuspected(LocalDateTime suspectedAt) {
        this.theftSuspectedAt = suspectedAt;
    }
}
