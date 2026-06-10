package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    // 공통 필드: 객체 식별자
    private UUID id;

    // 공통 필드: 객체 생성 시간
    private Instant createdAt;

    // 공통 필드: 객체 수정 시간
    private Instant updatedAt;

    // 어떤 사용자의 접속 상태인지 참조하기 위한 User의 id
    private UUID userId;

    // 사용자의 마지막 접속 시간
    private Instant lastActiveAt;

    // 생성자: UserStatus 객체를 생성할 때 userId를 받고, 마지막 접속 시간을 현재 시간으로 초기화
    public UserStatus(UUID userId) {
        validate(userId);

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = null;

        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    // 마지막 접속 시간을 현재 시간으로 갱신하는 메서드
    // 수정한 부분: Instant.now()를 한 번만 호출해서 lastActiveAt과 updatedAt을 같은 시간으로 맞춤
    public void updateLastActiveAt() {
        Instant now = Instant.now();

        this.lastActiveAt = now;
        this.updatedAt = now;
    }

    // 현재 온라인 상태인지 확인하는 메서드
    // 마지막 접속 시간이 현재 시간 기준 5분 이내이면 true 반환
    public boolean isOnline() {
        return lastActiveAt.plus(Duration.ofMinutes(5)).isAfter(Instant.now());
    }

    // userId가 null이면 잘못된 상태의 객체가 만들어지지 않도록 막는 검증 메서드
    private void validate(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 비어 있을 수 없습니다.");
        }
    }
}