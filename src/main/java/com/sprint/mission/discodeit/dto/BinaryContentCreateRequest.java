package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class BinaryContentCreateRequest {

    // 업로드할 파일의 이름
    private String fileName;

    // 업로드할 파일의 타입
    // 예: image/png, image/jpeg, application/pdf
    private String contentType;

    // 업로드할 파일의 실제 바이너리 데이터
    private byte[] bytes;

    // 생성자: BinaryContent 생성을 위해 필요한 파일 정보를 전달받음
    public BinaryContentCreateRequest(String fileName, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}