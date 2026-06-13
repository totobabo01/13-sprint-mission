package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {

    // 사용자 데이터를 메모리에 저장하는 Map
    private final Map<UUID, User> data;

    // 생성자
    public JCFUserService() {
        this.data = new HashMap<>();
    }

    // 사용자 생성
    // 수정한 부분: String 3개가 아니라 UserCreateRequest DTO를 받음
    @Override
    public UserResponse create(UserCreateRequest request) {
        // username 중복 검사
        if (existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // email 중복 검사
        if (existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        UUID id = user.getId();
        data.put(id, user);

        return toResponse(user);
    }

    // 사용자 단건 조회
    // 수정한 부분: User가 아니라 UserResponse를 반환
    @Override
    public UserResponse read(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 id입니다.");
        }

        return toResponse(user);
    }

    // 전체 사용자 조회
    // 수정한 부분: List<User>가 아니라 List<UserResponse>를 반환
    @Override
    public List<UserResponse> readAll() {
        List<UserResponse> allUsers = new ArrayList<>();

        for (User user : data.values()) {
            allUsers.add(toResponse(user));
        }

        return allUsers;
    }

    // 사용자 수정
    // 수정한 부분: id, username, email, password를 따로 받지 않고 UserUpdateRequest DTO를 받음
    @Override
    public UserResponse update(UserUpdateRequest request) {
        User user = data.get(request.getId());

        if (user == null) {
            throw new IllegalArgumentException("수정된 사용자 정보를 조회할 수 없습니다.");
        }

        // username이 바뀌는 경우에만 중복 검사
        if (!user.getUsername().equals(request.getUsername())
                && existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // email이 바뀌는 경우에만 중복 검사
        if (!user.getEmail().equals(request.getEmail())
                && existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        return toResponse(user);
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        if (data.get(id) == null) {
            throw new IllegalArgumentException("삭제할 사용자를 찾을 수 없습니다.");
        }

        data.remove(id);
    }

    // username 중복 검사 보조 메서드
    private boolean existsByUsername(String username) {
        for (User user : data.values()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    // email 중복 검사 보조 메서드
    private boolean existsByEmail(String email) {
        for (User user : data.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    // User 엔티티를 UserResponse DTO로 변환
    // password는 응답에 포함하지 않음
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                false
        );
    }
}