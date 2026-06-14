package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

// PRIVATE 채널 생성 요청 DTO
// PRIVATE 채널을 생성할 때 참여할 사용자 id 목록을 담는 클래스
@Getter
public class PrivateChannelCreateRequest {

    // PRIVATE 채널에 참여할 사용자 id 목록
    // 이 목록을 기준으로 User별 ReadStatus를 생성할 예정
    private List<UUID> participantUserIds;

    // PRIVATE 채널 생성 요청 객체를 생성하는 생성자
    public PrivateChannelCreateRequest(List<UUID> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }
}