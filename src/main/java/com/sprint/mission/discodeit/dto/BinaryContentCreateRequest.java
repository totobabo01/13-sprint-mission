package com.sprint.mission.discodeit.dto;

import lombok.Getter;

// BinaryContent 생성 요청 DTO
// 메시지 첨부파일이나 사용자 프로필 이미지 같은 바이너리 데이터를 생성할 때 사용하는 클래스
@Getter
public class BinaryContentCreateRequest {

    // 업로드할 파일의 이름
    // 예: profile.png, document.pdf
    private String fileName;

    // 업로드할 파일의 MIME 타입
    // 예: image/png, image/jpeg, application/pdf
    private String contentType;

    // 업로드할 파일의 실제 바이너리 데이터
    // byte 배열 형태로 파일 내용을 저장함
    private byte[] bytes;

    // BinaryContent 생성 요청 객체를 생성하는 생성자
    public BinaryContentCreateRequest(String fileName, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}