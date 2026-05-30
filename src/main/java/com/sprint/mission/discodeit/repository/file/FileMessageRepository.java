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
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> data = loadData();

        UUID id = message.getId();
        data.put(id, message);

        saveData(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Map<UUID, Message> data = loadData();
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> data = loadData();

        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(data.values());

        return allMessages;
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> data = loadData();

        data.remove(id);

        saveData(data);
    }

    @Override
    public boolean existsById(UUID id) {
        Map<UUID, Message> data = loadData();

        return data.containsKey(id);
    }
}