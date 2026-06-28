package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
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
    // PUBLIC 채널에서는 빈 리스트로 응답
    private List<UUID> participantUserIds;

    // 수정됨: 프론트엔드가 participantIds라는 이름을 기대할 수 있어서 추가
    // participantUserIds와 같은 값을 담는 호환용 필드
    private List<UUID> participantIds;

    // 수정됨: 프론트엔드가 participants라는 이름을 기대할 수 있어서 추가
    // participantUserIds와 같은 값을 담는 호환용 필드
    private List<UUID> participants;

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

        // 수정됨: null이면 프론트에서 .map() 호출 시 에러가 날 수 있으므로 빈 리스트로 보정
        List<UUID> safeParticipantUserIds = participantUserIds == null
                ? new ArrayList<>()
                : participantUserIds;

        this.participantUserIds = safeParticipantUserIds;

        // 수정됨: 프론트 호환용 필드에도 같은 값 넣기
        this.participantIds = safeParticipantUserIds;
        this.participants = safeParticipantUserIds;
    }
}