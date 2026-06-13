package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 메시지 응답 DTO
// 메시지 정보를 외부로 반환할 때 사용하는 클래스
// Entity인 Message를 그대로 반환하지 않고 필요한 데이터만 담아서 응답하기 위해 사용
@Getter
public class MessageResponse {

    // 메시지 id
    private UUID id;

    // 메시지 생성 시간
    private Instant createdAt;

    // 메시지 수정 시간
    private Instant updatedAt;

    // 메시지 내용
    private String content;

    // 메시지를 작성한 사용자 id
    private UUID authorId;

    // 메시지가 작성된 채널 id
    private UUID channelId;

    // 메시지에 첨부된 파일 id 목록
    private List<UUID> attachmentIds;

    // MessageResponse 객체를 생성하는 생성자
    public MessageResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String content,
            UUID authorId,
            UUID channelId,
            List<UUID> attachmentIds
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds;
    }
}