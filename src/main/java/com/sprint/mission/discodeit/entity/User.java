package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String username;
    private String email;
    private String password;
    private UUID profileId;


    public User(String username, String email, String password) {
        // 수정한 부분: 생성자에서 잘못된 값으로 User 객체가 생성되지 않도록 검증
        validate(username, email, password);

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = null;

        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = null;

    }

    public void update(String username, String email, String password) {
        // 수정한 부분: 수정할 때도 잘못된 값이 들어오지 않도록 검증
        validate(username, email, password);

        this.username = username;
        this.email = email;
        this.password = password;
        this.updatedAt = Instant.now();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    // 수정한 부분: 생성자와 update()에서 공통으로 사용할 입력값 검증 메서드 추가
    private void validate(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 비어 있을 수 없습니다.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
    }
}