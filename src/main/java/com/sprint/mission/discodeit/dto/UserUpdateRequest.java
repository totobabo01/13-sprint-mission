package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserUpdateRequest {

    // 수정할 사용자의 id
    private UUID id;

    // 수정할 사용자 이름
    private String username;

    // 수정할 이메일
    private String email;

    // 수정할 비밀번호
    private String password;

    // 선택적으로 교체할 프로필 이미지 정보
    // 프로필 이미지를 교체하지 않는 경우 null일 수 있음
    private BinaryContentCreateRequest profileImage;

    // 생성자: 사용자 수정에 필요한 값들을 하나의 DTO로 전달받음
    public UserUpdateRequest(UUID id, String username, String email, String password, BinaryContentCreateRequest profileImage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }
}