package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContentResponse {

    private UUID id;

    private Instant createdAt;

    private Instant updatedAt;

    private String fileName;

    private String contentType;

    // 실제 파일 데이터
    // JSON 응답에서는 Base64 문자열처럼 내려감
    private byte[] bytes;

    private Long size;

    public BinaryContentResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String fileName,
            String contentType,
            byte[] bytes,
            Long size
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = size;
    }
}