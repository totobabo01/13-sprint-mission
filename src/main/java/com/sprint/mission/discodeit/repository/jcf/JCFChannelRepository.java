package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    // 데이터 필드
    private final Map<UUID, Channel> data;

    // 생성자
    public JCFChannelRepository() {
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        UUID id = channel.getId();
        data.put(id, channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> allChannels = new ArrayList<>();
        allChannels.addAll(data.values());
        return allChannels;
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