package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileUserService implements UserService {

    private final Path filePath;

    // loadData
    private Map<UUID, User> loadData() {
        if(!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))){
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("사용자 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // saveData
    private void saveData(Map<UUID, User> data) {
        try {
            Files.createDirectories(filePath.getParent());

        try(ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("사용자 데이터 파일을 저장하는 중 오류가 발생했습니다.",e);
        }
    }

    public FileUserService(Path filePath) {
        this.filePath = filePath;
    }


    @Override
    public User create(String username, String email, String password) {
        Map<UUID, User> data = loadData();
        User user = new User(username, email, password);
        UUID id = user.getId();
        data.put(id, user);
        saveData(data);
        return user;
    }

    @Override
    public User read(UUID id) {
        Map<UUID, User> data = loadData();
        User user = data.get(id);
        if (user == null) {
            throw new IllegalArgumentException("조회할 사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    @Override
    public List<User> readAll() {
        Map<UUID, User> data = loadData();
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(data.values());
        return allUsers;
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        Map<UUID, User> data = loadData();
        User user = data.get(id);
        if(user == null) {
            throw new IllegalArgumentException("수정할 사용자를 찾을 수 없습니다.");
        }
        user.update(username, email, password);
        saveData(data);
        return user;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = loadData();
        User user = data.get(id);
        if(user == null) {
            throw new IllegalArgumentException("삭제할 사용자를 찾을 수 없습니다.");
        }
        data.remove(id);
        saveData(data);
    }
}
