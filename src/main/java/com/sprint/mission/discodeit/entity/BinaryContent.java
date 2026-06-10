package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    // 직렬화 버전 관리용 필드
    private static final long serialVersionUID = 1L;

    // 공통 필드: 바이너리 콘텐츠를 구분하기 위한 고유 id
    private UUID id;

    // 공통 필드: 바이너리 콘텐츠가 생성된 시간
    // BinaryContent는 수정 불가능한 도메인으로 보기 때문에 updatedAt은 정의하지 않음
    private Instant createdAt;

    // 파일 이름
    private String fileName;

    // 파일 타입
    // 예: image/png, image/jpeg, application/pdf
    private String contentType;

    // 실제 바이너리 데이터
    // 이미지, 파일 등의 실제 내용을 byte 배열로 저장
    private byte[] bytes;

    // 파일 크기
    // bytes.length 값을 저장
    private long size;

    // 생성자: 파일 이름, 파일 타입, 실제 데이터를 받아 BinaryContent 객체 생성
    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        // 잘못된 값으로 객체가 생성되지 않도록 먼저 검증
        validate(fileName, contentType, bytes);

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = bytes.length;
    }

    // 입력값 검증 메서드
    // fileName, contentType, bytes가 비어 있거나 잘못된 값이면 예외 발생
    private void validate(String fileName, String contentType, byte[] bytes) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일 이름은 비어 있을 수 없습니다.");
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("파일 타입은 비어 있을 수 없습니다.");
        }

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("파일 데이터는 비어 있을 수 없습니다.");
        }
    }
}