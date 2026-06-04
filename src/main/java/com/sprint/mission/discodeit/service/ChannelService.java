package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 채널 생성 기능
    public Channel create(ChannelType type, String name, String description);
    // 한 채널 읽기 기능
    public Channel read(UUID id);
    // 전체 채널 읽기 기능
    public List<Channel> readAll();
    // 채널 수정 기능
    public Channel update(UUID id, ChannelType type, String name, String description);
    // 채널 삭제 기능
    public void delete(UUID id);
}
