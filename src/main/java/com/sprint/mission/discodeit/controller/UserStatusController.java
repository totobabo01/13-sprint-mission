package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-statuses")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    // userId로 사용자 상태 조회
    @GetMapping("/{userId}")
    public UserStatusResponse findByUserId(@PathVariable UUID userId) {
        return userStatusService.findByUserId(userId);
    }

    // 사용자를 온라인 상태로 변경
    @PatchMapping("/{userId}/online")
    public UserStatusResponse updateOnline(@PathVariable UUID userId) {
        return userStatusService.updateOnline(userId);
    }

    // 사용자를 오프라인 상태로 변경
    @PatchMapping("/{userId}/offline")
    public UserStatusResponse updateOffline(@PathVariable UUID userId) {
        return userStatusService.updateOffline(userId);
    }

    // userId로 사용자 상태 삭제
    @DeleteMapping("/{userId}")
    public void deleteByUserId(@PathVariable UUID userId) {
        userStatusService.deleteByUserId(userId);
    }
}