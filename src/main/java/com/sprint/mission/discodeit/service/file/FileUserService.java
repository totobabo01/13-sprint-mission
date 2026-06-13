package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUserService implements UserService {

    private final Path filePath;

    public FileUserService(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 사용자 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("사용자 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // 사용자 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, User> data) {
        try {
            // 부모 폴더가 있는 경우에만 폴더 생성
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("사용자 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 사용자 생성
    // 수정한 부분: String 3개가 아니라 UserCreateRequest DTO를 받음
    @Override
    public UserResponse create(UserCreateRequest request) {
        Map<UUID, User> data = loadData();

        if (existsByUsername(data, request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        if (existsByEmail(data, request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        // 주의: 이 FileUserService는 구버전 구조라 profileImage 저장은 처리하지 않음
        // 현재 프로젝트 구조에서는 BasicUserService + BinaryContentRepository 사용을 권장
        data.put(user.getId(), user);

        saveData(data);

        return toResponse(user);
    }

    // 사용자 단건 조회
    // 수정한 부분: User가 아니라 UserResponse 반환
    @Override
    public UserResponse read(UUID id) {
        Map<UUID, User> data = loadData();

        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("조회할 사용자를 찾을 수 없습니다.");
        }

        return toResponse(user);
    }

    // 전체 사용자 조회
    // 수정한 부분: List<User>가 아니라 List<UserResponse> 반환
    @Override
    public List<UserResponse> readAll() {
        Map<UUID, User> data = loadData();

        List<UserResponse> responses = new ArrayList<>();

        for (User user : data.values()) {
            responses.add(toResponse(user));
        }

        return responses;
    }

    // 사용자 수정
    // 수정한 부분: id, username, email, password를 따로 받지 않고 UserUpdateRequest DTO를 받음
    @Override
    public UserResponse update(UserUpdateRequest request) {
        Map<UUID, User> data = loadData();

        User user = data.get(request.getId());

        if (user == null) {
            throw new IllegalArgumentException("수정할 사용자를 찾을 수 없습니다.");
        }

        if (!user.getUsername().equals(request.getUsername())
                && existsByUsername(data, request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        if (!user.getEmail().equals(request.getEmail())
                && existsByEmail(data, request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        // 주의: 이 FileUserService는 구버전 구조라 profileImage 교체는 처리하지 않음
        saveData(data);

        return toResponse(user);
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = loadData();

        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException("삭제할 사용자를 찾을 수 없습니다.");
        }

        data.remove(id);

        saveData(data);
    }

    // username 중복 검사 보조 메서드
    private boolean existsByUsername(Map<UUID, User> data, String username) {
        for (User user : data.values()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    // email 중복 검사 보조 메서드
    private boolean existsByEmail(Map<UUID, User> data, String email) {
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