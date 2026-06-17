package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 파일 기반 UserStatusRepository 구현체
// UserStatus 데이터를 객체 직렬화를 사용해 파일에 저장함
public class FileUserStatusRepository implements UserStatusRepository {

    // UserStatus 데이터를 저장할 파일 경로
    private final Path filePath;

    // 생성자: 저장 파일 경로를 외부에서 주입받음
    public FileUserStatusRepository(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 UserStatus 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap을 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, UserStatus> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("사용자 상태 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // UserStatus 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, UserStatus> data) {
        try {
            // 수정한 부분:
            // filePath.getParent()가 null일 수 있으므로 null이 아닐 때만 폴더 생성
            createParentDirectoryIfNeeded();

            // Map 전체를 직렬화해서 파일에 저장
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("사용자 상태 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 부모 폴더가 있는 경우에만 생성하는 보조 메서드
    private void createParentDirectoryIfNeeded() {
        Path parent = filePath.getParent();

        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                throw new RuntimeException("사용자 상태 데이터 파일의 상위 폴더를 생성하는 중 오류가 발생했습니다.", e);
            }
        }
    }

    // UserStatus 저장
    // 새 UserStatus면 추가, 같은 id가 있으면 덮어쓰기
    @Override
    public UserStatus save(UserStatus userStatus) {
        Map<UUID, UserStatus> data = loadData();

        UUID id = userStatus.getId();
        data.put(id, userStatus);

        saveData(data);
        return userStatus;
    }

    // id로 UserStatus 단건 조회
    @Override
    public UserStatus findById(UUID id) {
        Map<UUID, UserStatus> data = loadData();

        return data.get(id);
    }

    // 전체 UserStatus 조회
    @Override
    public List<UserStatus> findAll() {
        Map<UUID, UserStatus> data = loadData();

        return new ArrayList<>(data.values());
    }

    // id로 UserStatus 삭제
    @Override
    public void deleteById(UUID id) {
        Map<UUID, UserStatus> data = loadData();

        data.remove(id);

        saveData(data);
    }

    // id에 해당하는 UserStatus가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        Map<UUID, UserStatus> data = loadData();

        return data.containsKey(id);
    }

    // userId로 UserStatus 조회
    // UserStatus의 id가 아니라, 어떤 User의 상태인지 나타내는 userId로 찾는 메서드
    @Override
    public UserStatus findByUserId(UUID userId) {
        Map<UUID, UserStatus> data = loadData();

        for (UserStatus userStatus : data.values()) {
            if (userStatus.getUserId().equals(userId)) {
                return userStatus;
            }
        }

        return null;
    }

    // 추가한 부분:
    // userId에 해당하는 UserStatus가 존재하는지 확인
    @Override
    public boolean existsByUserId(UUID userId) {
        return findByUserId(userId) != null;
    }

    // userId로 UserStatus 삭제
    // 먼저 userId가 같은 UserStatus를 찾고, 실제 삭제는 UserStatus의 id로 삭제
    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> data = loadData();

        UserStatus target = null;

        for (UserStatus userStatus : data.values()) {
            if (userStatus.getUserId().equals(userId)) {
                target = userStatus;
                break;
            }
        }

        if (target != null) {
            data.remove(target.getId());
            saveData(data);
        }
    }
}