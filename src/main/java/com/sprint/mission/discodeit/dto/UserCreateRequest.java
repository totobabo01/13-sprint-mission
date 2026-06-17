package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class UserCreateRequest {

    // 생성할 사용자의 이름
    private String username;

    // 생성할 사용자의 이메일
    private String email;

    // 생성할 사용자의 비밀번호
    private String password;

    // 선택적으로 등록할 프로필 이미지 정보
    // 프로필 이미지를 등록하지 않는 경우 null일 수 있음
    private BinaryContentCreateRequest profileImage;

    // 생성자: 사용자 생성에 필요한 값들을 하나의 DTO로 전달받음
    public UserCreateRequest(String username, String email, String password, BinaryContentCreateRequest profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }
}