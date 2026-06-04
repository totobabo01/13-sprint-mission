package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageService implements MessageService {

    private final Path filePath;

    private final UserService userService;
    private final ChannelService channelService;

    // loadData
    private Map<UUID, Message> loadData() {
        if(!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))){
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // saveData
    private void saveData(Map<UUID, Message> data) {
        try {
            Files.createDirectories(filePath.getParent());

            try(ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생했습니다.",e);
        }
    }

    public FileMessageService(Path filePath, UserService userService, ChannelService channelService) {
        this.filePath = filePath;
        this.userService = userService;
        this.channelService = channelService;
    }


    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        Map<UUID,Message> data = loadData();
        userService.read(authorId);
        channelService.read(channelId);

        Message message = new Message(content, authorId, channelId);
        UUID id = message.getId();
        data.put(id, message);
        saveData(data);
        return message;
    }

    @Override
    public Message read(UUID id) {
        Map<UUID,Message> data = loadData();
        Message message = data.get(id);
        if (message == null) {
            throw new IllegalArgumentException("조회할 메시지를 찾을 수 없습니다.");
        }
        return message;
    }

    @Override
    public List<Message> readAll() {
        Map<UUID, Message> data = loadData();
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(data.values());
        return allMessages;
    }

    @Override
    public Message update(UUID id, String content) {
        Map<UUID, Message> data = loadData();
        Message message = data.get(id);
        if(message == null) {
            throw new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다.");
        }
        message.update(content);
        saveData(data);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = loadData();
        Message message = data.get(id);
        if (message == null) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }
        data.remove(id);
        saveData(data);
    }
}
