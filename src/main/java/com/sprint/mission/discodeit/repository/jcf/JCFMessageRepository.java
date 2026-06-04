package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    // 데이터 필드
    private final Map<UUID, Message> data;

    // 생성자
    public JCFMessageRepository() {
        data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        UUID id = message.getId();
        data.put(id, message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message message = data.get(id);
        return message;
    }

    @Override
    public List<Message> findAll() {
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(data.values());
        return allMessages;
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}