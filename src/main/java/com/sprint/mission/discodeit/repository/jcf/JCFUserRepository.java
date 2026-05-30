package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    // 데이터 필드
    private final Map<UUID, User> data;

    // 생성자
    public JCFUserRepository() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        UUID id = user.getId();
        data.put(id, user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = data.get(id);
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(data.values());
        return allUsers;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
