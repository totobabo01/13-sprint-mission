package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String content;
    private UUID authorId;
    private UUID channelId;

    public Message(String content, UUID authorId, UUID channelId) {
        validate(content, authorId, channelId);

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;

        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public void update(String content) {
        validateContent(content);

        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    private void validate(String content, UUID authorId, UUID channelId) {
        validateContent(content);

        if (authorId == null) {
            throw new IllegalArgumentException("작성자 ID는 비어 있을 수 없습니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 비어 있을 수 없습니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
    }
}