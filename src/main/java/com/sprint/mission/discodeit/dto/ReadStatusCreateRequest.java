package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

// ReadStatus 생성 요청 DTO
// 특정 User가 특정 Channel에 대한 읽음 상태를 생성할 때 필요한 데이터를 담는 클래스
@Getter
public class ReadStatusCreateRequest {

    // 읽음 상태를 생성할 사용자 id
    private UUID userId;

    // 읽음 상태를 생성할 채널 id
    private UUID channelId;

    // ReadStatus 생성 요청 객체를 생성하는 생성자
    public ReadStatusCreateRequest(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }
}