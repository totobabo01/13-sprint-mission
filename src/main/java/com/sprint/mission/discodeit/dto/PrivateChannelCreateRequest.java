package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// PRIVATE 채널 생성 요청 DTO
// PRIVATE 채널을 생성할 때 참여할 사용자 id 목록을 담는 클래스
@Getter
@NoArgsConstructor // 수정됨: JSON 요청 바인딩을 위해 기본 생성자 추가
public class PrivateChannelCreateRequest {

    // PRIVATE 채널에 참여할 사용자 id 목록
    // 수정됨: 프론트가 participantIds, participants, userIds, memberIds 등으로 보낼 가능성 대비
    @JsonAlias({"participantIds", "participants", "userIds", "memberIds"})
    private List<UUID> participantUserIds;

    // PRIVATE 채널 생성 요청 객체를 생성하는 생성자
    public PrivateChannelCreateRequest(List<UUID> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }
}