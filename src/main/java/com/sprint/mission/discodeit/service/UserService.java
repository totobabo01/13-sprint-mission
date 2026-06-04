package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 유저 생성 기능
    public User create(String username, String email, String password);
    // 유저 하나 읽기 기능
    public User read(UUID id);
    // 유저 전부 읽기 기능
    public List<User> readAll();
    // 유저 수정 기능
    public User update(UUID id, String username, String email, String password);
    // 유저 삭제 기능
    public void delete(UUID id);

}
