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
        validate(username, email, password);

        this.username = username;
        this.email = email;
        this.password = password;
        this.updatedAt = Instant.now();
    }

    // profileId까지 함께 수정할 때 사용
    public void update(String username, String email, String password, UUID profileId) {
        validate(username, email, password);

        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }

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