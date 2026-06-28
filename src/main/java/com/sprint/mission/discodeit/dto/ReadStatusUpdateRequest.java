package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

// ReadStatus 수정 요청 DTO
// 특정 ReadStatus의 마지막 읽은 시간을 수정할 때 필요한 데이터를 담는 클래스
@Getter
@NoArgsConstructor // 수정됨: JSON 요청 바인딩을 위해 기본 생성자 추가
public class ReadStatusUpdateRequest {

    // 수정할 ReadStatus의 id
    private UUID id;

    // 마지막으로 읽은 시간
    // 수정됨: 프론트가 newLastActiveAt, lastActiveAt 등으로 보내는 경우도 받도록 처리
    @JsonAlias({"newLastActiveAt", "lastActiveAt", "lastReadAt"})
    private Instant lastReadAt;

    // ReadStatus 수정 요청 객체를 생성하는 생성자
    public ReadStatusUpdateRequest(UUID id, Instant lastReadAt) {
        this.id = id;
        this.lastReadAt = lastReadAt;
    }
}