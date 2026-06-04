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

    private final Path filePath;

    public FileMessageRepository(Path filePath) {
        this.filePath = filePath;
    }

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

    private void saveData(Map<UUID, Message> data) {
        try {
            // 수정한 부분: filePath.getParent()가 null일 수 있으므로 별도 메서드로 안전하게 처리
            createParentDirectoryIfNeeded();

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 수정한 부분: parent가 없는 상대 경로를 사용할 경우 NullPointerException을 막기 위한 방어 코드
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

    @Override
    public Message save(Message message) {
        // 수정한 부분: File Repository는 호출할 때마다 전체 파일을 읽고 다시 저장하는 구조임을 명시
        // 이번 과제 범위에서는 단순하고 이해하기 쉬운 방식이지만,
        // 데이터가 많아지면 파일 전체를 매번 읽고 쓰기 때문에 비용이 커질 수 있음
        Map<UUID, Message> data = loadData();

        UUID id = message.getId();
        data.put(id, message);

        saveData(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        // 수정한 부분: 조회 시에도 전체 파일을 읽은 뒤 Map에서 id로 찾는 구조
        Map<UUID, Message> data = loadData();
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        // 수정한 부분: 전체 조회 역시 파일 전체를 읽어 List로 변환하는 구조
        Map<UUID, Message> data = loadData();

        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(data.values());

        return allMessages;
    }

    @Override
    public void deleteById(UUID id) {
        // 수정한 부분: 삭제 시 전체 파일을 읽고, Map에서 삭제한 뒤 다시 전체 저장
        Map<UUID, Message> data = loadData();

        data.remove(id);

        saveData(data);
    }

    @Override
    public boolean existsById(UUID id) {
        // 수정한 부분: 존재 여부 확인도 파일 전체를 읽은 뒤 containsKey로 확인하는 구조
        Map<UUID, Message> data = loadData();

        return data.containsKey(id);
    }
}