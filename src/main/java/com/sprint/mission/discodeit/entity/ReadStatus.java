package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    // 공통 필드: 객체 식별자
    private UUID id;

    // 공통 필드: 객체 생성 시간
    private Instant createdAt;

    // 공통 필드: 객체 수정 시간
    private Instant updatedAt;

    // 어떤 사용자의 읽음 상태인지 참조하기 위한 User의 id
    private UUID userId;

    // 어떤 채널에 대한 읽음 상태인지 참조하기 위한 Channel의 id
    private UUID channelId;

    // 해당 사용자가 해당 채널을 마지막으로 읽은 시간
    private Instant lastReadAt;

    // 생성자: 사용자 id와 채널 id를 받아 ReadStatus 객체를 생성
    public ReadStatus(UUID userId, UUID channelId) {
        validate(userId, channelId);

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = null;

        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = Instant.now();
    }

    // 마지막 읽은 시간을 현재 시간으로 갱신하는 메서드
    // 수정한 부분: lastActiveAt이 아니라 ReadStatus의 필드인 lastReadAt을 갱신해야 함
    public void updateLastReadAt() {
        Instant now = Instant.now();

        this.lastReadAt = now;
        this.updatedAt = now;
    }

    // userId와 channelId가 null이면 잘못된 상태의 객체가 만들어지지 않도록 막는 검증 메서드
    // 수정한 부분: userId와 channelId를 각각 나누어 검사해서 예외 메시지를 명확하게 함
    private void validate(UUID userId, UUID channelId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 비어 있을 수 없습니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 비어 있을 수 없습니다.");
        }
    }
}