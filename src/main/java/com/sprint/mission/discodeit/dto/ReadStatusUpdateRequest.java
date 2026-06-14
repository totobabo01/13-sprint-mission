package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// ReadStatus 수정 요청 DTO
// 특정 ReadStatus의 마지막 읽은 시간을 수정할 때 필요한 데이터를 담는 클래스
@Getter
public class ReadStatusUpdateRequest {

    // 수정할 ReadStatus의 id
    private UUID id;

    // 마지막으로 읽은 시간
    private Instant lastReadAt;

    // ReadStatus 수정 요청 객체를 생성하는 생성자
    public ReadStatusUpdateRequest(UUID id, Instant lastReadAt) {
        this.id = id;
        this.lastReadAt = lastReadAt;
    }
}