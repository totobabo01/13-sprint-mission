package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
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
    // PUBLIC 채널에서는 값이 있고, PRIVATE 채널에서는 null일 수 있음
    private String name;

    // 채널 설명
    // PUBLIC 채널에서는 값이 있고, PRIVATE 채널에서는 null일 수 있음
    private String description;

    // 해당 채널의 가장 최근 메시지 생성 시간
    // 메시지가 없으면 null일 수 있음
    private Instant lastMessageAt;

    // PRIVATE 채널에 참여한 사용자 id 목록
    // PUBLIC 채널에서는 null 또는 빈 리스트일 수 있음
    private List<UUID> participantUserIds;

    // ChannelResponse 객체를 생성하는 생성자
    public ChannelResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description,
            Instant lastMessageAt,
            List<UUID> participantUserIds
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.name = name;
        this.description = description;
        this.lastMessageAt = lastMessageAt;
        this.participantUserIds = participantUserIds;
    }
}