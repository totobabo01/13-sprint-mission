package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
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

    // 추가한 부분:
    // 사용자의 온라인 여부
    // true면 온라인, false면 오프라인
    private boolean online;

    // 사용자의 마지막 활동 시간
    // 로그인, 상태 변경, 활동 갱신 시점 등을 기록
    private Instant lastActiveAt;

    // 생성자: UserStatus 객체를 생성할 때 userId를 받고 기본 온라인 상태로 생성
    public UserStatus(UUID userId) {
        validate(userId);

        Instant now = Instant.now();

        this.id = UUID.randomUUID();
        this.createdAt = now;
        this.updatedAt = null;

        this.userId = userId;

        // 새로 생성된 상태 정보는 온라인으로 시작
        this.online = true;

        // 마지막 활동 시간은 생성 시점으로 초기화
        this.lastActiveAt = now;
    }

    // 사용자를 온라인 상태로 변경하는 메서드
    // 로그인 성공 또는 활동 감지 시 호출
    public void updateOnline() {
        Instant now = Instant.now();

        this.online = true;
        this.lastActiveAt = now;
        this.updatedAt = now;
    }

    // 사용자를 오프라인 상태로 변경하는 메서드
    // 로그아웃 또는 접속 종료 시 호출
    public void updateOffline() {
        Instant now = Instant.now();

        this.online = false;
        this.lastActiveAt = now;
        this.updatedAt = now;
    }

    // 마지막 접속 시간을 현재 시간으로 갱신하는 메서드
    // 기존 코드와의 호환을 위해 유지
    // 활동이 있었다는 의미이므로 온라인 상태도 true로 변경
    public void updateLastActiveAt() {
        Instant now = Instant.now();

        this.online = true;
        this.lastActiveAt = now;
        this.updatedAt = now;
    }

    // 현재 온라인 상태인지 확인하는 메서드
    // 기존에는 5분 이내 접속 기준이었지만,
    // 고도화 후에는 online 필드 값을 기준으로 판단
    public boolean isOnline() {
        return online;
    }

    // userId가 null이면 잘못된 상태의 객체가 만들어지지 않도록 막는 검증 메서드
    private void validate(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 비어 있을 수 없습니다.");
        }
    }
}