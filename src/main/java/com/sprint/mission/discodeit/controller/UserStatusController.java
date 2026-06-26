package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-statuses")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    // userId로 사용자 상태 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserStatusResponse> findByUserId(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // 사용자를 온라인 상태로 변경
    @PatchMapping("/{userId}/online")
    public ResponseEntity<UserStatusResponse> updateOnline(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.updateOnline(userId);

        return ResponseEntity.ok(response);
    }

    // 사용자를 오프라인 상태로 변경
    @PatchMapping("/{userId}/offline")
    public ResponseEntity<UserStatusResponse> updateOffline(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.updateOffline(userId);

        return ResponseEntity.ok(response);
    }

    // userId로 사용자 상태 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteByUserId(
            @PathVariable UUID userId
    ) {
        userStatusService.deleteByUserId(userId);

        return ResponseEntity
                .noContent()
                .build();
    }
}