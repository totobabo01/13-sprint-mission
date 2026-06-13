package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// 채널 응답 DTO
// 채널 정보를 외부로 반환할 때 사용하는 클래스
// Entity인 Channel을 그대로 반환하지 않고, 필요한 데이터만 담아서 응답하기 위해 사용
@Getter
public class ChannelResponse {

    // 채널 id
    private UUID id;

    // 채널 생성 시간
    private Instant createdAt;

    // 채널 수정 시간
    private Instant updatedAt;

    // 채널 종류
    // 예: PUBLIC, PRIVATE
    private ChannelType type;

    // 채널 이름
    private String name;

    // 채널 설명
    private String description;

    // ChannelResponse 객체를 생성하는 생성자
    public ChannelResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.name = name;
        this.description = description;
    }
}