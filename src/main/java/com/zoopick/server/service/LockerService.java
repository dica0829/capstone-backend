package com.zoopick.server.service;

import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.zoopick.server.entity.Locker;
import com.zoopick.server.entity.LockerCommand;
import com.zoopick.server.locker.CommandStatus;
import com.zoopick.server.locker.LockerCommandType;
import com.zoopick.server.locker.LockerStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zoopick.server.repository.LockerCommandRepository;
import com.zoopick.server.repository.LockerRepository;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockerService {
    private final LockerRepository lockerRepository;
    private final LockerCommandRepository commandRepository;

    @Transactional
    public LockerCommand requestUnlock(Long lockerId, Long itemId) {
        Locker locker = lockerRepository.findById(lockerId).orElseThrow(() -> new IllegalArgumentException("Locker not found: " + lockerId));

        if (locker.getStatus() == LockerStatus.MAINTENANCE) {
            throw new IllegalArgumentException("사물함 점검 중입니다");
        }

        boolean isStorage = (locker.getCurrentItemId() == null);

        if (isStorage) {
            if (itemId == null) {
                throw new IllegalArgumentException("보관할 item_id가 필요합니다");
            }
            locker.setStatus(LockerStatus.IN_USE);
            locker.setCurrentItemId(itemId);
            //itemRepository update status는 어떻게 처리를 해야하나
            log.info("[STORE]: locker_id={}, item_id={}", lockerId, itemId);
        } else {
            Long storedItemId = locker.getCurrentItemId();
            locker.setStatus(LockerStatus.EMPTY);
            locker.setCurrentItemId(null);

            log.info("[RETRIEVE] locker_id={}, item_id={} 회수 요청", lockerId, storedItemId);
        }

        return enqueue(lockerId, LockerCommandType.OPEN);
    }

    private LockerCommand enqueue(Long lockerId, LockerCommandType type) {
        return commandRepository.save(LockerCommand.builder()
                .lockerId(lockerId)
                .command(type)
                .status(CommandStatus.PENDING)
                .createdAt(Instant.now())
                .build());
    }

    @Transactional
    public LockerCommand requestLock(Long lockerId) {
        lockerRepository.findById(lockerId).orElseThrow(() -> new IllegalArgumentException("Locker not found: " + lockerId));
        log.info("[CLOSE] locker_id={} 잠금요청", lockerId);
        return enqueue(lockerId, LockerCommandType.CLOSE);
    }

    @Transactional
    public Optional<LockerCommand> pollNextCommand(Long lockerId) {
        Optional<LockerCommand> next = commandRepository.findFirstByLockerIdAndStatusOrderByCreatedAtAsc(lockerId, CommandStatus.PENDING);

        next.ifPresent(cmd -> {
            cmd.setStatus(CommandStatus.CONSUMED);
            cmd.setConsumedAt(Instant.now());
            log.info("[POLL] locker_id={}, command={} type={} consumed", lockerId, cmd, cmd.getCommand());
        });
        return next;
    }

    @Transactional
    public void ackCommand(Long lockerId, Long commandId) {
        LockerCommand cmd =commandRepository.findById(commandId).orElseThrow(() -> new IllegalArgumentException("Command not found: " + commandId));

        if(!cmd.getLockerId().equals(lockerId)) {
            throw new IllegalArgumentException("locker_id 불일치");
        }
        cmd.setStatus(CommandStatus.COMPLETED);
        cmd.setCompletedAt(Instant.now());
        log.info("[ACK] locker_id={}, command={} type={} consumed", lockerId, cmd, cmd.getCommand());
    }

}
