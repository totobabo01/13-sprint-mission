package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

// 메시지 생성 요청 DTO
// 메시지를 생성할 때 필요한 데이터를 담는 클래스
@Getter
public class MessageCreateRequest {

    // 메시지 내용
    private String content;

    // 메시지를 작성한 사용자 id
    private UUID authorId;

    // 메시지가 작성될 채널 id
    private UUID channelId;

    // 메시지 생성 요청 객체를 생성하는 생성자
    public MessageCreateRequest(String content, UUID authorId, UUID channelId) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }
}