package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
// 수정됨: class 레벨 @RequestMapping 제거
// 이유: 기존 RESTful 경로(/api/user-statuses/...)와
//      프론트가 요청하는 경로(/api/user/{userId}/userStatus, /api/users/{userId}/userStatus)를
//      함께 처리하기 위해서
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    // 기존 방식: userId로 사용자 상태 조회
    // GET /api/user-statuses/{userId}
    @GetMapping("/api/user-statuses/{userId}")
    public ResponseEntity<UserStatusResponse> findByUserId(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // 수정됨: 프론트엔드 복수 users 경로 추가
    // GET /api/users/{userId}/userStatus
    @GetMapping("/api/users/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> findByUserIdForFrontendUsers(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // 수정됨: 프론트엔드 단수 user 경로 추가
    // GET /api/user/{userId}/userStatus
    @GetMapping("/api/user/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> findByUserIdForFrontendUser(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.findByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // 기존 방식: 사용자를 온라인 상태로 변경
    // PATCH /api/user-statuses/{userId}/online
    @PatchMapping("/api/user-statuses/{userId}/online")
    public ResponseEntity<UserStatusResponse> updateOnline(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.updateOnline(userId);

        return ResponseEntity.ok(response);
    }

    // 기존 방식: 사용자를 오프라인 상태로 변경
    // PATCH /api/user-statuses/{userId}/offline
    @PatchMapping("/api/user-statuses/{userId}/offline")
    public ResponseEntity<UserStatusResponse> updateOffline(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.updateOffline(userId);

        return ResponseEntity.ok(response);
    }

    // 수정됨: 프론트엔드 복수 users 경로 추가
    // PATCH /api/users/{userId}/userStatus
    // 로그인 후 사용자 상태를 온라인으로 변경할 때 사용
    @PatchMapping("/api/users/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> updateUserStatusForFrontendUsers(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.updateOnline(userId);

        return ResponseEntity.ok(response);
    }

    // 수정됨: 프론트엔드 단수 user 경로 추가
    // PATCH /api/user/{userId}/userStatus
    // 현재 콘솔에서 404가 나던 경로
    @PatchMapping("/api/user/{userId}/userStatus")
    public ResponseEntity<UserStatusResponse> updateUserStatusForFrontendUser(
            @PathVariable UUID userId
    ) {
        UserStatusResponse response = userStatusService.updateOnline(userId);

        return ResponseEntity.ok(response);
    }

    // 기존 방식: userId로 사용자 상태 삭제
    // DELETE /api/user-statuses/{userId}
    @DeleteMapping("/api/user-statuses/{userId}")
    public ResponseEntity<Void> deleteByUserId(
            @PathVariable UUID userId
    ) {
        userStatusService.deleteByUserId(userId);

        return ResponseEntity
                .noContent()
                .build();
    }
}