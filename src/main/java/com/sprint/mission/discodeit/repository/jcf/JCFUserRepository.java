package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// JCF 기반 UserRepository 구현체
// discodeit.repository.type=jcf 일 때만 Bean으로 등록됨
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf"
)
public class JCFUserRepository implements UserRepository {

    // User 데이터를 메모리에 저장하는 Map
    // key: User의 id
    // value: User 객체
    private final Map<UUID, User> data;

    // 생성자: User 데이터를 저장할 HashMap 초기화
    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    // User 저장
    // 새 User라면 추가되고, 같은 id의 User가 있으면 덮어쓰기됨
    @Override
    public User save(User user) {
        UUID id = user.getId();
        data.put(id, user);

        return user;
    }

    // id로 User 단건 조회
    // 해당 id가 없으면 null 반환
    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    // 저장된 모든 User 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // id로 User 삭제
    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    // id에 해당하는 User가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    // username이 이미 사용 중인지 확인
    // 모든 User를 순회하면서 같은 username이 있으면 true 반환
    @Override
    public boolean existsByUsername(String username) {
        for (User user : data.values()) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    // email이 이미 사용 중인지 확인
    // 모든 User를 순회하면서 같은 email이 있으면 true 반환
    @Override
    public boolean existsByEmail(String email) {
        for (User user : data.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    // username으로 User 조회
    // AuthService 로그인 기능에서 사용
    // username이 일치하는 User가 있으면 해당 User 반환
    // 없으면 null 반환
    @Override
    public User findByUsername(String username) {
        for (User user : data.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }

        return null;
    }
}