package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// BinaryContent 응답 DTO
// 파일 정보나 첨부파일 정보를 외부로 반환할 때 사용하는 클래스
// 실제 bytes 데이터는 응답에 포함하지 않고, 파일의 기본 정보만 반환함
@Getter
public class BinaryContentResponse {

    // BinaryContent id
    private UUID id;

    // BinaryContent 생성 시간
    private Instant createdAt;

    // BinaryContent 수정 시간
    private Instant updatedAt;

    // 파일 이름
    // 예: profile.png, document.pdf
    private String fileName;

    // 파일 MIME 타입
    // 예: image/png, image/jpeg, application/pdf
    private String contentType;

    // 파일 크기
    // bytes 배열의 길이 값을 저장
    private Long size;

    // BinaryContentResponse 객체를 생성하는 생성자
    public BinaryContentResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String fileName,
            String contentType,
            Long size
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }
}