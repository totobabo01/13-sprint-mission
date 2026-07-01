package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 채널 수정 요청 DTO
// 어떤 채널을 어떻게 수정할지에 대한 정보를 담는 클래스
@Getter
@NoArgsConstructor // 수정됨: JSON 요청 바인딩을 위해 기본 생성자 추가
public class ChannelUpdateRequest {

    // 수정할 채널의 id
    private UUID id;

    // 수정할 채널 종류
    // 예: PUBLIC, PRIVATE
    private ChannelType type;

    // 수정할 채널 이름
    // 수정됨: 프론트가 title/channelName으로 보낼 가능성 대비
    @JsonAlias({"title", "channelName"})
    private String name;

    // 수정할 채널 설명
    // 수정됨: 프론트가 desc로 보낼 가능성 대비
    @JsonAlias({"desc"})
    private String description;

    // 채널 수정 요청 객체를 생성하는 생성자
    public ChannelUpdateRequest(UUID id, ChannelType type, String name, String description) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }
}