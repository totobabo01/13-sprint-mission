package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String content;
    private UUID authorId;
    private UUID channelId;

    public Message(String content, UUID authorId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;

        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
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

    public String getContent() {
        return content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}