package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 수정됨: JSON 요청 바인딩을 위해 기본 생성자 추가
public class ChannelCreateRequest {

    private ChannelType type;

    @JsonAlias({"title", "channelName"})
    private String name;

    @JsonAlias({"desc"})
    private String description;

    public ChannelCreateRequest(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }
}