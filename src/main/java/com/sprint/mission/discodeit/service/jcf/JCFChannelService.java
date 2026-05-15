package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    // 데이터 필드
    private final Map<UUID, Channel> data;

    // 생성자
    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        UUID id = channel.getId();
        data.put(id, channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        Channel readChannel = data.get(id);
        if (readChannel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 id입니다.");
        }
        return readChannel;
    }

    @Override
    public List<Channel> readAll() {
        List<Channel> allChannels = new ArrayList<>();
        allChannels.addAll(data.values());
        return allChannels;
    }

    @Override
    public Channel update(UUID id, ChannelType type, String name, String description) {
        Channel updateChannel = data.get(id);
        if (updateChannel == null) {
            throw new IllegalArgumentException("수정된 채널 정보를 조회할 수 없습니다.");
        }
        updateChannel.update(type, name, description);
        return updateChannel;
    }

    @Override
    public void delete(UUID id) {
        if(data.get(id) == null) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }
        data.remove(id);
    }
}
