package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// File 기반 UserRepository 구현체
// discodeit.repository.type=file 이거나 설정이 없을 때 Bean으로 등록됨
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = true
)
public class FileUserRepository implements UserRepository {

    // 사용자 데이터를 저장할 파일 경로
    private final Path filePath;

    // Spring 자동 Bean 등록용 생성자
    // 생성자가 여러 개 있을 때 Spring이 이 생성자를 사용하도록 @Autowired를 붙임
    @Autowired
    public FileUserRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Path.of(fileDirectory, "spring-users.ser");
    }

    // JavaApplication에서 직접 new FileUserRepository(Path.of(...)) 할 때 사용 가능
    public FileUserRepository(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 사용자 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap을 반환
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
            createParentDirectoryIfNeeded();

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("사용자 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 부모 폴더가 있는 경우에만 생성하는 보조 메서드
    private void createParentDirectoryIfNeeded() {
        Path parent = filePath.getParent();

        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                throw new RuntimeException("사용자 데이터 파일의 상위 폴더를 생성하는 중 오류가 발생했습니다.", e);
            }
        }
    }

    // 사용자 저장
    // 새 사용자면 추가, 같은 id가 있으면 덮어쓰기
    @Override
    public User save(User user) {
        Map<UUID, User> data = loadData();

        UUID id = user.getId();
        data.put(id, user);

        saveData(data);
        return user;
    }

    // id로 사용자 단건 조회
    @Override
    public User findById(UUID id) {
        if (id == null) {
            return null;
        }

        Map<UUID, User> data = loadData();

        for (User user : data.values()) {
            if (user.getId() != null && user.getId().equals(id)) {
                return user;
            }
        }

        return null;
    }

    // 전체 사용자 조회
    @Override
    public List<User> findAll() {
        Map<UUID, User> data = loadData();

        return new ArrayList<>(data.values());
    }

    // id로 사용자 삭제
    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = loadData();

        data.remove(id);

        saveData(data);
    }

    // id에 해당하는 사용자가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        return findById(id) != null;
    }

    // username이 이미 사용 중인지 확인
    @Override
    public boolean existsByUsername(String username) {
        Map<UUID, User> data = loadData();

        for (User user : data.values()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    // email이 이미 사용 중인지 확인
    @Override
    public boolean existsByEmail(String email) {
        Map<UUID, User> data = loadData();

        for (User user : data.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    // username으로 사용자 조회
    // AuthService에서 로그인할 때 사용
    @Override
    public User findByUsername(String username) {
        Map<UUID, User> data = loadData();

        for (User user : data.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }
}