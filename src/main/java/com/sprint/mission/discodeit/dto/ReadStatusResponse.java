package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// ReadStatus 응답 DTO
// 특정 User가 특정 Channel을 어디까지 읽었는지에 대한 정보를 반환할 때 사용하는 클래스
@Getter
public class ReadStatusResponse {

    // ReadStatus id
    private UUID id;

    // ReadStatus 생성 시간
    private Instant createdAt;

    // ReadStatus 수정 시간
    private Instant updatedAt;

    // 읽음 상태를 가진 사용자 id
    private UUID userId;

    // 읽음 상태가 연결된 채널 id
    private UUID channelId;

    // 마지막으로 읽은 시간
    private Instant lastReadAt;

    // ReadStatusResponse 객체를 생성하는 생성자
    public ReadStatusResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }
}