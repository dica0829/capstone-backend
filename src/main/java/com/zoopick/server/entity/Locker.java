package com.zoopick.server.entity;

import com.zoopick.server.locker.LockerStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lockers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locker {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LockerStatus status;

    @Column(name = "current_item_id")
    private Long currentItemId;
}
