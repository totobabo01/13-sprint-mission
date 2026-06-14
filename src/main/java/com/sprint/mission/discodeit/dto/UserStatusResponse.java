package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// UserStatus 정보를 응답으로 전달하기 위한 DTO
// Entity를 직접 반환하지 않고 필요한 데이터만 담아서 반환함
@Getter
public class UserStatusResponse {

    // UserStatus 자체의 고유 id
    private UUID id;

    // UserStatus가 생성된 시간
    private Instant createdAt;

    // UserStatus가 수정된 시간
    private Instant updatedAt;

    // 상태 정보가 연결된 User의 id
    private UUID userId;

    // 사용자의 온라인 여부
    private boolean online;

    // 사용자가 마지막으로 접속했거나 상태가 변경된 시간
    private Instant lastActiveAt;

    // 생성자
    // Service에서 UserStatus 엔티티를 UserStatusResponse로 변환할 때 사용
    public UserStatusResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            boolean online,
            Instant lastActiveAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.online = online;
        this.lastActiveAt = lastActiveAt;
    }
}