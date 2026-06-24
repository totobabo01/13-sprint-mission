package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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

// File 기반 ChannelRepository 구현체
// discodeit.repository.type=file 이거나 설정이 없을 때 Bean으로 등록됨
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = true
)
public class FileChannelRepository implements ChannelRepository {

    // 채널 데이터를 저장할 파일 경로
    private final Path filePath;

    // 추가한 부분:
    // Spring 자동 Bean 등록용 생성자
    // 생성자가 여러 개 있을 때 Spring이 이 생성자를 사용하도록 @Autowired를 붙임
    @Autowired
    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Path.of(fileDirectory, "spring-channels.ser");
    }

    // 기존 생성자 유지:
    // JavaApplication에서 직접 new FileChannelRepository(Path.of(...)) 할 때 사용 가능
    public FileChannelRepository(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 Channel 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap을 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // Channel 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, Channel> data) {
        try {
            // 수정한 부분:
            // filePath.getParent()가 null일 수 있으므로 null이 아닐 때만 폴더 생성
            createParentDirectoryIfNeeded();

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 부모 폴더가 있는 경우에만 생성하는 보조 메서드
    private void createParentDirectoryIfNeeded() {
        Path parent = filePath.getParent();

        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                throw new RuntimeException("채널 데이터 파일의 상위 폴더를 생성하는 중 오류가 발생했습니다.", e);
            }
        }
    }

    // Channel 저장
    // 새 Channel이면 추가, 같은 id가 있으면 덮어쓰기
    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = loadData();

        UUID id = channel.getId();
        data.put(id, channel);

        saveData(data);
        return channel;
    }

    // id로 Channel 단건 조회
    @Override
    public Channel findById(UUID id) {
        Map<UUID, Channel> data = loadData();

        return data.get(id);
    }

    // 전체 Channel 조회
    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> data = loadData();

        return new ArrayList<>(data.values());
    }

    // id로 Channel 삭제
    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> data = loadData();

        data.remove(id);

        saveData(data);
    }

    // id에 해당하는 Channel이 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        Map<UUID, Channel> data = loadData();

        return data.containsKey(id);
    }
}