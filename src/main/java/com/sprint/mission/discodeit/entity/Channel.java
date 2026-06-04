package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        // 수정한 부분: 생성자에서 잘못된 값으로 Channel 객체가 생성되지 않도록 검증
        validate(type, name, description);

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;

        this.type = type;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void update(ChannelType type, String name, String description) {
        // 수정한 부분: 수정할 때도 잘못된 값이 들어오지 않도록 검증
        validate(type, name, description);

        this.type = type;
        this.name = name;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    // 수정한 부분: 생성자와 update()에서 공통으로 사용할 입력값 검증 메서드 추가
    private void validate(ChannelType type, String name, String description) {
        if (type == null) {
            throw new IllegalArgumentException("채널 종류는 비어 있을 수 없습니다.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어 있을 수 없습니다.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("채널 설명은 비어 있을 수 없습니다.");
        }
    }
}