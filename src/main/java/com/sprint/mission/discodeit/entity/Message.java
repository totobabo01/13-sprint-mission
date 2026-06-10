package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {

    // 직렬화 버전 관리용 필드
    private static final long serialVersionUID = 1L;

    // 공통 필드: 메시지 객체를 구분하기 위한 고유 id
    private UUID id;

    // 공통 필드: 메시지가 생성된 시간
    private Instant createdAt;

    // 공통 필드: 메시지가 수정된 시간
    private Instant updatedAt;

    // 메시지 내용
    private String content;

    // 메시지를 작성한 User의 id
    private UUID authorId;

    // 메시지가 작성된 Channel의 id
    private UUID channelId;

    // 메시지에 첨부된 BinaryContent들의 id 목록
    // BinaryContent 객체를 직접 참조하지 않고 UUID로 참조
    private List<UUID> attachmentIds;

    // 생성자: 메시지 내용, 작성자 id, 채널 id를 받아 Message 객체 생성
    public Message(String content, UUID authorId, UUID channelId) {
        // 잘못된 값으로 메시지가 생성되지 않도록 검증
        validate(content, authorId, channelId);

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = null;

        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;

        // 첨부파일은 처음에는 없을 수 있으므로 빈 리스트로 초기화
        this.attachmentIds = new ArrayList<>();
    }

    // 메시지 내용을 수정하는 메서드
    public void update(String content) {
        // 수정할 메시지 내용이 비어 있지 않은지 검증
        validateContent(content);

        this.content = content;
        this.updatedAt = Instant.now();
    }

    // 메시지 생성 시 필요한 값들을 검증하는 메서드
    private void validate(String content, UUID authorId, UUID channelId) {
        validateContent(content);

        if (authorId == null) {
            throw new IllegalArgumentException("작성자 ID는 비어 있을 수 없습니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 비어 있을 수 없습니다.");
        }
    }

    // 메시지 내용이 null, 빈 문자열, 공백 문자열인지 검증하는 메서드
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
    }

    // 메시지에 첨부파일 id를 추가하는 메서드
    public void addAttachment(UUID binaryContentId) {
        // 첨부파일 id가 없으면 잘못된 참조이므로 예외 발생
        if (binaryContentId == null) {
            throw new IllegalArgumentException("첨부파일 ID는 비어 있을 수 없습니다.");
        }

        // BinaryContent 객체 자체가 아니라 BinaryContent의 id만 저장
        attachmentIds.add(binaryContentId);

        // 첨부파일 목록이 변경되었으므로 수정 시간 갱신
        updatedAt = Instant.now();
    }
}