package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.util.UUID;

// 채널 수정 요청 DTO
// 어떤 채널을 어떻게 수정할지에 대한 정보를 담는 클래스
@Getter
public class ChannelUpdateRequest {

    // 수정할 채널의 id
    private UUID id;

    // 수정할 채널 종류
    // 예: PUBLIC, PRIVATE
    private ChannelType type;

    // 수정할 채널 이름
    private String name;

    // 수정할 채널 설명
    private String description;

    // 채널 수정 요청 객체를 생성하는 생성자
    public ChannelUpdateRequest(UUID id, ChannelType type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }
}