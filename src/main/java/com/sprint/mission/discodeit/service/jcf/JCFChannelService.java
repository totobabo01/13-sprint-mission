package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    // 채널 데이터를 메모리에 저장하는 Map
    private final Map<UUID, Channel> data;

    // 생성자
    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    // 채널 생성
    // 수정한 부분: ChannelType, name, description을 따로 받지 않고 ChannelCreateRequest DTO를 받음
    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 생성 요청은 비어 있을 수 없습니다.");
        }

        Channel channel = new Channel(
                request.getType(),
                request.getName(),
                request.getDescription()
        );

        UUID id = channel.getId();
        data.put(id, channel);

        return toResponse(channel);
    }

    // 채널 단건 조회
    // 수정한 부분: Channel 엔티티가 아니라 ChannelResponse 반환
    @Override
    public ChannelResponse read(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 id입니다.");
        }

        return toResponse(channel);
    }

    // 전체 채널 조회
    // 수정한 부분: List<Channel>이 아니라 List<ChannelResponse> 반환
    @Override
    public List<ChannelResponse> readAll() {
        List<ChannelResponse> allChannels = new ArrayList<>();

        for (Channel channel : data.values()) {
            allChannels.add(toResponse(channel));
        }

        return allChannels;
    }

    // 채널 수정
    // 수정한 부분: id, type, name, description을 따로 받지 않고 ChannelUpdateRequest DTO를 받음
    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 수정 요청은 비어 있을 수 없습니다.");
        }

        Channel channel = data.get(request.getId());

        if (channel == null) {
            throw new IllegalArgumentException("수정된 채널 정보를 조회할 수 없습니다.");
        }

        channel.update(
                request.getType(),
                request.getName(),
                request.getDescription()
        );

        return toResponse(channel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        if (data.get(id) == null) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }

        data.remove(id);
    }

    // Channel 엔티티를 ChannelResponse DTO로 변환하는 보조 메서드
    private ChannelResponse toResponse(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription()
        );
    }
}