package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

// 채널 생성 요청 DTO
// 사용자가 채널을 생성할 때 필요한 데이터를 담는 클래스
@Getter
public class ChannelCreateRequest {

    // 채널 종류
    // 예: PUBLIC, PRIVATE
    private ChannelType type;

    // 채널 이름
    private String name;

    // 채널 설명
    private String description;

    // 채널 생성 요청 객체를 만들 때 필요한 값들을 초기화하는 생성자
    public ChannelCreateRequest(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }
}