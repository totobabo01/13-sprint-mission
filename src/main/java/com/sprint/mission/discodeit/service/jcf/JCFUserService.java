package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    // 데이터 필드
    private final Map<UUID, User> data;

    // 생성자
    public JCFUserService() {
        data = new HashMap<>();
    }

    @Override
    public User create(String username, String email, String password) {
        User user = new User(username, email, password);
        UUID id = user.getId();
        data.put(id, user);
        return user;
    }

    @Override
    public User read(UUID id) {
        User readUser = data.get(id);
        if (readUser == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 id입니다.");
        }
        return readUser;
    }

    @Override
    public List<User> readAll() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(data.values());
        return allUsers;
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        User updateUser = data.get(id);
        if (updateUser == null) {
            throw new IllegalArgumentException("수정된 사용자 정보를 조회할 수 없습니다.");
        }
        updateUser.update(username, email, password);
        return updateUser;
    }

    @Override
    public void delete(UUID id) {
        if(data.get(id) == null) {
            throw new IllegalArgumentException("삭제할 사용자를 찾을 수 없습니다.");
        }
        data.remove(id);
    }
}
