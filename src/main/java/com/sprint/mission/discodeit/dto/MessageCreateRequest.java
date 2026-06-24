package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// 메시지 생성 요청 DTO
// 메시지를 생성할 때 필요한 데이터를 담는 클래스
@Getter
@NoArgsConstructor
public class MessageCreateRequest {

    // 메시지 내용
    private String content;

    // 메시지를 작성한 사용자 id
    private UUID authorId;

    // 메시지가 작성될 채널 id
    private UUID channelId;

    // 첨부파일 생성 요청 목록
    // 첨부파일이 없으면 null 또는 빈 리스트일 수 있음
    private List<BinaryContentCreateRequest> attachments;

    // 메시지 생성 요청 객체를 생성하는 생성자
    // 첨부파일이 있는 메시지를 생성할 때 사용
    public MessageCreateRequest(
            String content,
            UUID authorId,
            UUID channelId,
            List<BinaryContentCreateRequest> attachments
    ) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachments = attachments;
    }

    // 기존 테스트 코드 호환용 생성자
    // 첨부파일 없이 메시지를 생성할 때 사용
    public MessageCreateRequest(String content, UUID authorId, UUID channelId) {
        this(content, authorId, channelId, null);
    }
}