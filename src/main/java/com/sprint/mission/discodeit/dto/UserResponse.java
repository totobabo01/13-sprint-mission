package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserResponse {

    // 사용자 id
    private UUID id;

    // 사용자 생성 시간
    private Instant createdAt;

    // 사용자 수정 시간
    private Instant updatedAt;

    // 사용자 이름
    private String username;

    // 사용자 이메일
    private String email;

    // 프로필 이미지로 연결된 BinaryContent의 id
    private UUID profileId;

    // 수정됨: 프론트엔드에서 profileImageId라는 이름을 기대할 수도 있어서 추가
    // profileId와 같은 값을 담는 호환용 필드
    private UUID profileImageId;

    // 현재 온라인 상태 여부
    private boolean online;

    // 생성자: 외부에 보여줄 사용자 응답 정보를 생성
    // password는 보안상 응답에 포함하지 않음
    public UserResponse(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String username,
            String email,
            UUID profileId,
            boolean online
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.username = username;
        this.email = email;
        this.profileId = profileId;

        // 수정됨: profileId와 동일한 값을 profileImageId에도 넣음
        this.profileImageId = profileId;

        this.online = online;
    }
}