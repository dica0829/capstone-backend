package com.zoopick.server.controller;

import com.zoopick.server.entity.LockerCommand;
import com.zoopick.server.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/lockers")
@RequiredArgsConstructor
public class LockerController {

    private final LockerService lockerService;

    @PostMapping("/{lockerId}/unlock")
    public ResponseEntity<Map<String, Object>> unlock(
            @PathVariable Long lockerId,
            @RequestBody(required = false) UnlockRequest req) {

        Long itemId = (req != null) ? req.itemId() : null;
        LockerCommand cmd = lockerService.requestUnlock(lockerId, itemId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "command_id", cmd.getId(),
                "command", cmd.getCommand(),
                "message", "자물쇠가 곧 열립니다"
        ));
    }

    @PostMapping("/{lockerId}/lock")
    public ResponseEntity<Map<String, Object>> lock(@PathVariable Long lockerId) {
        LockerCommand cmd = lockerService.requestLock(lockerId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "command_id", cmd.getId(),
                "command", cmd.getCommand(),
                "message", "자물쇠가 곧 잠깁니다"
        ));
    }

    @GetMapping("/{lockerId}/pending")
    public ResponseEntity<Map<String, Object>> pollCommand(@PathVariable Long lockerId) {
        Optional<LockerCommand> cmd = lockerService.pollNextCommand(lockerId);
        if (cmd.isEmpty()) {
            return ResponseEntity.ok(Map.of("command", "NONE"));
        }
        return ResponseEntity.ok(Map.of(
                "command", cmd.get().getCommand().name(),
                "command_id", cmd.get().getId()
        ));
    }

    @PostMapping("/{lockerId}/ack/{commandId}")
    public ResponseEntity<Void> ack(
            @PathVariable Long lockerId,
            @PathVariable Long commandId) {
        lockerService.ackCommand(lockerId, commandId);
        return ResponseEntity.ok().build();
    }

    public record UnlockRequest(Long itemId) {
    }
}