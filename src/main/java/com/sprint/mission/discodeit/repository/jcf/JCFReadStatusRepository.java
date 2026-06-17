package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ReadStatus 데이터를 메모리에 저장하고 조회하는 Repository 구현체
// JCF의 HashMap을 사용해서 데이터를 저장
public class JCFReadStatusRepository implements ReadStatusRepository {

    // ReadStatus 데이터를 저장하는 Map
    // key: ReadStatus id
    // value: ReadStatus 객체
    private final Map<UUID, ReadStatus> data;

    // 생성자: ReadStatus 데이터를 저장할 HashMap 초기화
    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    // ReadStatus 저장
    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);

        return readStatus;
    }

    // id로 ReadStatus 조회
    @Override
    public ReadStatus findById(UUID id) {
        return data.get(id);
    }

    // 모든 ReadStatus 조회
    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    // id로 ReadStatus 삭제
    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    // id에 해당하는 ReadStatus가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    // 특정 User와 특정 Channel에 해당하는 ReadStatus 조회
    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus readStatus : data.values()) {
            if (
                    readStatus.getUserId().equals(userId)
                            && readStatus.getChannelId().equals(channelId)
            ) {
                return readStatus;
            }
        }

        return null;
    }

    // 특정 User의 모든 ReadStatus 조회
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getUserId().equals(userId)) {
                result.add(readStatus);
            }
        }

        return result;
    }

    // 특정 Channel의 모든 ReadStatus 조회
    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        List<ReadStatus> result = new ArrayList<>();

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                result.add(readStatus);
            }
        }

        return result;
    }

    // 특정 Channel과 관련된 모든 ReadStatus 삭제
    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
    }
}