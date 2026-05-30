package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private final Path filePath;

    public FileChannelRepository(Path filePath) {
        this.filePath = filePath;
    }

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

    private void saveData(Map<UUID, Channel> data) {
        try {
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = loadData();

        UUID id = channel.getId();
        data.put(id, channel);

        saveData(data);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Map<UUID, Channel> data = loadData();
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> data = loadData();

        List<Channel> allChannels = new ArrayList<>();
        allChannels.addAll(data.values());

        return allChannels;
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> data = loadData();

        data.remove(id);

        saveData(data);
    }

    @Override
    public boolean existsById(UUID id) {
        Map<UUID, Channel> data = loadData();

        return data.containsKey(id);
    }
}