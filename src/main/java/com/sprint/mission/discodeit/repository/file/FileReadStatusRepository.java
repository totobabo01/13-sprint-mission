package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ReadStatus 데이터를 파일에 저장하고 조회하는 Repository 구현체
// 객체 직렬화를 사용해서 Map<UUID, ReadStatus> 형태로 파일에 저장
public class FileReadStatusRepository implements ReadStatusRepository {

    // ReadStatus 데이터를 저장할 파일 경로
    private final Path filePath;

    // 생성자: 저장 파일 경로를 외부에서 주입받음
    public FileReadStatusRepository(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 ReadStatus 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, ReadStatus> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, ReadStatus>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("읽음 상태 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // ReadStatus 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, ReadStatus> data) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("읽음 상태 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // ReadStatus 저장
    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> data = loadData();

        data.put(readStatus.getId(), readStatus);

        saveData(data);
        return readStatus;
    }

    // id로 ReadStatus 조회
    @Override
    public ReadStatus findById(UUID id) {
        Map<UUID, ReadStatus> data = loadData();

        return data.get(id);
    }

    // 모든 ReadStatus 조회
    @Override
    public List<ReadStatus> findAll() {
        Map<UUID, ReadStatus> data = loadData();

        return new ArrayList<>(data.values());
    }

    // id로 ReadStatus 삭제
    @Override
    public void deleteById(UUID id) {
        Map<UUID, ReadStatus> data = loadData();

        data.remove(id);

        saveData(data);
    }

    // id에 해당하는 ReadStatus가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        Map<UUID, ReadStatus> data = loadData();

        return data.containsKey(id);
    }

    // 특정 User와 특정 Channel에 해당하는 ReadStatus 조회
    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        Map<UUID, ReadStatus> data = loadData();

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
        Map<UUID, ReadStatus> data = loadData();

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
        Map<UUID, ReadStatus> data = loadData();

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
        Map<UUID, ReadStatus> data = loadData();

        data.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));

        saveData(data);
    }
}