package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    // 수정할 사용자의 id
    private UUID id;

    // 수정할 사용자 이름
    private String username;

    // 수정할 이메일
    private String email;

    // 수정할 비밀번호
    private String password;

    // 이미 업로드된 BinaryContent의 id
    private UUID profileId;

    // 프로필 이미지를 새로 업로드해서 교체하는 경우 사용
    private BinaryContentCreateRequest profileImage;

    public UserUpdateRequest(
            UUID id,
            String username,
            String email,
            String password,
            UUID profileId,
            BinaryContentCreateRequest profileImage
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
        this.profileImage = profileImage;
    }
}