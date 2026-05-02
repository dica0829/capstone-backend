package com.zoopick.server.repository;

import com.zoopick.server.entity.LockerCommand;
import com.zoopick.server.locker.CommandStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface LockerCommandRepository extends JpaRepository<LockerCommand, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LockerCommand> findFirstByLockerIdAndStatusOrderByCreatedAtAsc(
            Long lockerId, CommandStatus status
    );
}
