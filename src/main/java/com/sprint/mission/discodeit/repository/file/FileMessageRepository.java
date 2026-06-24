package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// File 기반 MessageRepository 구현체
// discodeit.repository.type=file 이거나 설정이 없을 때 Bean으로 등록됨
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = true
)
public class FileMessageRepository implements MessageRepository {

    // 메시지 데이터를 저장할 파일 경로
    private final Path filePath;

    // Spring 자동 Bean 등록용 생성자
    @Autowired
    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Path.of(fileDirectory, "spring-messages.ser");
    }

    // 기존 JavaApplication 테스트용 생성자
    public FileMessageRepository(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 메시지 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // 메시지 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, Message> data) {
        try {
            createParentDirectoryIfNeeded();

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // parent가 없는 상대 경로를 사용할 경우 NullPointerException을 막기 위한 방어 코드
    private void createParentDirectoryIfNeeded() {
        Path parent = filePath.getParent();

        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                throw new RuntimeException("메시지 데이터 파일의 상위 폴더를 생성하는 중 오류가 발생했습니다.", e);
            }
        }
    }

    // 메시지 저장
    // 새 메시지면 추가, 같은 id의 메시지가 있으면 덮어쓰기
    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("저장할 메시지는 null일 수 없습니다.");
        }

        Map<UUID, Message> data = loadData();

        UUID id = message.getId();

        if (id == null) {
            throw new IllegalArgumentException("저장할 메시지 id는 null일 수 없습니다.");
        }

        data.put(id, message);

        saveData(data);
        return message;
    }

    // id로 메시지 단건 조회
    // Map key가 아니라 Message 객체 내부 id 기준으로 조회하도록 수정
    @Override
    public Message findById(UUID id) {
        if (id == null) {
            return null;
        }

        Map<UUID, Message> data = loadData();

        for (Message message : data.values()) {
            if (message.getId() != null && message.getId().equals(id)) {
                return message;
            }
        }

        return null;
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAll() {
        Map<UUID, Message> data = loadData();

        return new ArrayList<>(data.values());
    }

    // id로 메시지 삭제
    // Map key가 어긋난 경우도 대비해서 Message 객체 내부 id 기준으로 삭제
    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            return;
        }

        Map<UUID, Message> data = loadData();

        data.entrySet().removeIf(entry -> {
            Message message = entry.getValue();
            return message != null
                    && message.getId() != null
                    && message.getId().equals(id);
        });

        saveData(data);
    }

    // id에 해당하는 메시지가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        return findById(id) != null;
    }

    // 특정 Channel에 작성된 메시지 목록 조회
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Map<UUID, Message> data = loadData();

        List<Message> result = new ArrayList<>();

        for (Message message : data.values()) {
            if (message.getChannelId() != null && message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }

        return result;
    }

    // 특정 Channel의 가장 최근 메시지 생성 시간 조회
    // 메시지가 없으면 null 반환
    @Override
    public Instant findLastMessageAtByChannelId(UUID channelId) {
        Map<UUID, Message> data = loadData();

        Instant lastMessageAt = null;

        for (Message message : data.values()) {
            if (message.getChannelId() != null && message.getChannelId().equals(channelId)) {
                if (lastMessageAt == null || message.getCreatedAt().isAfter(lastMessageAt)) {
                    lastMessageAt = message.getCreatedAt();
                }
            }
        }

        return lastMessageAt;
    }

    // 특정 Channel에 작성된 모든 메시지 삭제
    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, Message> data = loadData();

        data.values().removeIf(message ->
                message.getChannelId() != null && message.getChannelId().equals(channelId)
        );

        saveData(data);
    }

    // 특정 User가 작성한 모든 메시지 삭제
    @Override
    public void deleteByAuthorId(UUID authorId) {
        Map<UUID, Message> data = loadData();

        data.values().removeIf(message ->
                message.getAuthorId() != null && message.getAuthorId().equals(authorId)
        );

        saveData(data);
    }
}