package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        validate(type, name, description);

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = null;

        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(ChannelType type, String name, String description) {
        validate(type, name, description);

        this.type = type;
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    private void validate(ChannelType type, String name, String description) {
        if (type == null) {
            throw new IllegalArgumentException("채널 종류는 비어 있을 수 없습니다.");
        }

        // 수정한 부분:
        // PRIVATE 채널은 name, description을 사용하지 않음
        // 그래서 null이어도 허용하고 바로 검증 종료
        if (type == ChannelType.PRIVATE) {
            return;
        }

        // PUBLIC 채널은 name 필수
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어 있을 수 없습니다.");
        }

        // PUBLIC 채널은 description 필수
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널 설명은 비어 있을 수 없습니다.");
        }
    }
}