package com.zoopick.server.entity;

import com.zoopick.server.locker.LockerCommandType;
import com.zoopick.server.locker.LockerStatus;
import io.lettuce.core.protocol.CommandType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "locker_commands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "locker_id", nullable = false)
    private Long lockerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LockerCommandType command; // open / close

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LockerStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
