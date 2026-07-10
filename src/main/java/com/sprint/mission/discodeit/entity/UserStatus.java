package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

    // 어떤 사용자의 접속 상태인지 참조하기 위한 User의 id
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    // 사용자의 온라인 여부
    @Column(name = "online", nullable = false)
    private boolean online;

    // 사용자의 마지막 활동 시간
    @Column(name = "last_active_at", nullable = false)
    private Instant lastActiveAt;

    // 생성자: UserStatus 객체를 생성할 때 userId를 받고 기본 온라인 상태로 생성
    public UserStatus(UUID userId) {
        validate(userId);

        this.userId = userId;
        this.online = true;
        this.lastActiveAt = Instant.now();
    }

    // 사용자를 온라인 상태로 변경하는 메서드
    public void updateOnline() {
        this.online = true;
        this.lastActiveAt = Instant.now();
        markUpdated();
    }

    // 사용자를 오프라인 상태로 변경하는 메서드
    public void updateOffline() {
        this.online = false;
        this.lastActiveAt = Instant.now();
        markUpdated();
    }

    // 마지막 접속 시간을 현재 시간으로 갱신하는 메서드
    // 기존 코드와의 호환을 위해 유지
    public void updateLastActiveAt() {
        this.online = true;
        this.lastActiveAt = Instant.now();
        markUpdated();
    }

    // 필요하면 특정 시간으로 갱신할 때 사용
    public void updateLastActiveAt(Instant lastActiveAt) {
        if (lastActiveAt == null) {
            throw new IllegalArgumentException("마지막 활동 시간은 비어 있을 수 없습니다.");
        }

        this.online = true;
        this.lastActiveAt = lastActiveAt;
        markUpdated();
    }

    public boolean isOnline() {
        return online;
    }

    private void validate(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 비어 있을 수 없습니다.");
        }
    }
}