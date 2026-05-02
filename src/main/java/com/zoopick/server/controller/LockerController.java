package com.zoopick.server.controller;

import com.zoopick.server.entity.Locker;
import com.zoopick.server.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lockers")
@RequiredArgsConstructor
public class LockerController {
    private final LockerService lockerService;

}
