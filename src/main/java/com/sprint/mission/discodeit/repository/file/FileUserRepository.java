package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

    private final Path filePath;

    public FileUserRepository(Path filePath) {
        this.filePath = filePath;
    }

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

    private void saveData(Map<UUID, User> data) {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("사용자 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public User save(User user) {
        Map<UUID, User> data = loadData();

        UUID id = user.getId();
        data.put(id, user);

        saveData(data);
        return user;
    }

    @Override
    public User findById(UUID id) {
        Map<UUID, User> data = loadData();
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        Map<UUID, User> data = loadData();

        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(data.values());

        return allUsers;
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> data = loadData();

        data.remove(id);

        saveData(data);
    }

    @Override
    public boolean existsById(UUID id) {
        Map<UUID, User> data = loadData();

        return data.containsKey(id);
    }
}