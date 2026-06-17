package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {

    // 메시지 데이터를 저장할 파일 경로
    private final Path filePath;

    // 생성자: 저장 파일 경로를 외부에서 주입받음
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
            // filePath.getParent()가 null일 수 있으므로 안전하게 처리
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
        Map<UUID, Message> data = loadData();

        UUID id = message.getId();
        data.put(id, message);

        saveData(data);
        return message;
    }

    // id로 메시지 단건 조회
    @Override
    public Message findById(UUID id) {
        Map<UUID, Message> data = loadData();

        return data.get(id);
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAll() {
        Map<UUID, Message> data = loadData();

        return new ArrayList<>(data.values());
    }

    // id로 메시지 삭제
    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> data = loadData();

        data.remove(id);

        saveData(data);
    }

    // id에 해당하는 메시지가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        Map<UUID, Message> data = loadData();

        return data.containsKey(id);
    }

    // 특정 Channel에 작성된 메시지 목록 조회
    // ChannelResponse에서 가장 최근 메시지 시간을 구할 때 사용
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Map<UUID, Message> data = loadData();

        List<Message> result = new ArrayList<>();

        for (Message message : data.values()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }

        return result;
    }

    // 특정 Channel에 작성된 모든 메시지 삭제
    // Channel 삭제 시 관련 Message도 같이 삭제하기 위해 사용
    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, Message> data = loadData();

        data.values().removeIf(message -> message.getChannelId().equals(channelId));

        saveData(data);
    }
}