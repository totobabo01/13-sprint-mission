package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException("조회할 채널을 찾을 수 없습니다.");
        }

        return channel;
    }

    @Override
    public List<Channel> readAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, ChannelType type, String name, String description) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException("수정할 채널을 찾을 수 없습니다.");
        }

        channel.update(type, name, description);
        channelRepository.save(channel);

        return channel;
    }

    @Override
    public void delete(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }

        channelRepository.deleteById(id);
    }
}