package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

// 메시지 수정 요청 DTO
// 어떤 메시지를 어떤 내용으로 수정할지에 대한 데이터를 담는 클래스
@Getter
public class MessageUpdateRequest {

    // 수정할 메시지의 id
    private UUID id;

    // 수정할 메시지 내용
    private String content;

    // 메시지 수정 요청 객체를 생성하는 생성자
    public MessageUpdateRequest(UUID id, String content) {
        this.id = id;
        this.content = content;
    }
}