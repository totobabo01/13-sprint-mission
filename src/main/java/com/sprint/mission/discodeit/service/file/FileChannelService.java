package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final Path filePath;

    // loadData
    private Map<UUID, Channel> loadData() {
        if(!Files.exists(filePath)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))){
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // saveData
    private void saveData(Map<UUID, Channel> data) {
        try {
            Files.createDirectories(filePath.getParent());

            try(ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 파일을 저장하는 중 오류가 발생했습니다.",e);
        }
    }

    public FileChannelService(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Map<UUID, Channel> data = loadData();
        Channel channel = new Channel(type, name, description);
        UUID id = channel.getId();
        data.put(id, channel);
        saveData(data);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        Map<UUID, Channel> data = loadData();
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("조회할 채널을 찾을 수 없습니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> readAll() {
        Map<UUID, Channel> data = loadData();
        List<Channel> allChannels = new ArrayList<>();
        allChannels.addAll(data.values());
        return allChannels;
    }

    @Override
    public Channel update(UUID id, ChannelType type, String name, String description) {
        Map<UUID, Channel> data = loadData();
        Channel channel = data.get(id);
        if(channel == null) {
            throw new IllegalArgumentException("수정할 채널을 찾을 수 없습니다.");
        }
        channel.update(type, name, description);
        saveData(data);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = loadData();
        Channel channel = data.get(id);
        if(channel == null) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }
        data.remove(id);
        saveData(data);
    }
}
