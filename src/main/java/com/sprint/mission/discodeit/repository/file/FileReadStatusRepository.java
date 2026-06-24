package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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

// ReadStatus 데이터를 파일에 저장하고 조회하는 Repository 구현체
// 객체 직렬화를 사용해서 Map<UUID, ReadStatus> 형태로 파일에 저장
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = true
)
public class FileReadStatusRepository implements ReadStatusRepository {

    // ReadStatus 데이터를 저장할 파일 경로
    private final Path filePath;

    // Spring 자동 Bean 등록용 생성자
    // 생성자가 여러 개 있을 때 Spring이 이 생성자를 사용하도록 @Autowired를 붙임
    @Autowired
    public FileReadStatusRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Path.of(fileDirectory, "spring-read-statuses.ser");
    }

    // 기존 생성자 유지:
    // JavaApplication에서 직접 new FileReadStatusRepository(Path.of(...)) 할 때 사용 가능
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
            createParentDirectoryIfNeeded();

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("읽음 상태 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 부모 폴더가 있는 경우에만 생성하는 보조 메서드
    private void createParentDirectoryIfNeeded() {
        Path parent = filePath.getParent();

        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                throw new RuntimeException("읽음 상태 데이터 파일의 상위 폴더를 생성하는 중 오류가 발생했습니다.", e);
            }
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

    // 추가한 부분: 특정 User가 참여한 Channel id 목록 조회
    // BasicChannelService에서 PRIVATE 채널 조회 권한을 판단할 때 사용
    @Override
    public List<UUID> findChannelIdsByUserId(UUID userId) {
        Map<UUID, ReadStatus> data = loadData();

        List<UUID> channelIds = new ArrayList<>();

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getUserId().equals(userId)) {
                channelIds.add(readStatus.getChannelId());
            }
        }

        return channelIds;
    }

    // 추가한 부분: 특정 Channel에 참여한 User id 목록 조회
    // ChannelResponse의 participantUserIds를 만들 때 사용
    @Override
    public List<UUID> findUserIdsByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = loadData();

        List<UUID> userIds = new ArrayList<>();

        for (ReadStatus readStatus : data.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                userIds.add(readStatus.getUserId());
            }
        }

        return userIds;
    }

    // 특정 Channel과 관련된 모든 ReadStatus 삭제
    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = loadData();

        data.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));

        saveData(data);
    }

    // 추가한 부분: 특정 User와 관련된 모든 ReadStatus 삭제
    // User 삭제 시 관련 ReadStatus도 같이 삭제하기 위해 사용
    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, ReadStatus> data = loadData();

        data.values().removeIf(readStatus -> readStatus.getUserId().equals(userId));

        saveData(data);
    }
}