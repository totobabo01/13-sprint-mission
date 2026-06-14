package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// JCF 기반 UserStatusRepository 구현체
// UserStatus 데이터를 파일이 아니라 메모리의 HashMap에 저장함
public class JCFUserStatusRepository implements UserStatusRepository {

    // UserStatus 데이터를 저장하는 Map
    // key: UserStatus id
    // value: UserStatus 객체
    private final Map<UUID, UserStatus> data;

    // 생성자
    // UserStatus 데이터를 저장할 HashMap 초기화
    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    // UserStatus 저장
    // 새 UserStatus면 추가되고, 같은 id가 있으면 덮어쓰기됨
    @Override
    public UserStatus save(UserStatus userStatus) {
        UUID id = userStatus.getId();
        data.put(id, userStatus);

        return userStatus;
    }

    // UserStatus id로 단건 조회
    // 해당 id의 UserStatus가 없으면 null 반환
    @Override
    public UserStatus findById(UUID id) {
        return data.get(id);
    }

    // 저장된 모든 UserStatus 조회
    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    // UserStatus id로 삭제
    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    // UserStatus id가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    // userId로 UserStatus 조회
    // UserStatus 자체 id가 아니라 어떤 User의 상태인지 나타내는 userId로 찾음
    @Override
    public UserStatus findByUserId(UUID userId) {
        for (UserStatus userStatus : data.values()) {
            if (userStatus.getUserId().equals(userId)) {
                return userStatus;
            }
        }

        return null;
    }

    // userId에 해당하는 UserStatus가 존재하는지 확인
    @Override
    public boolean existsByUserId(UUID userId) {
        return findByUserId(userId) != null;
    }

    // userId로 UserStatus 삭제
    // 먼저 userId가 같은 UserStatus를 찾고, 실제 삭제는 UserStatus id로 삭제
    @Override
    public void deleteByUserId(UUID userId) {
        UserStatus target = findByUserId(userId);

        if (target != null) {
            data.remove(target.getId());
        }
    }
}