package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    // 채널 생성
    // 수정한 부분: ChannelType, name, description을 따로 받지 않고 ChannelCreateRequest DTO로 받음
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

        channelRepository.save(channel);

        // 수정한 부분: Channel 엔티티를 그대로 반환하지 않고 ChannelResponse로 변환해서 반환
        return toResponse(channel);
    }

    // 채널 단건 조회
    // 수정한 부분: Channel 엔티티가 아니라 ChannelResponse 반환
    @Override
    public ChannelResponse read(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException("조회할 채널을 찾을 수 없습니다.");
        }

        return toResponse(channel);
    }

    // 전체 채널 조회
    // 수정한 부분: List<Channel>이 아니라 List<ChannelResponse> 반환
    @Override
    public List<ChannelResponse> readAll() {
        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponse> responses = new ArrayList<>();

        for (Channel channel : channels) {
            responses.add(toResponse(channel));
        }

        return responses;
    }

    // 채널 수정
    // 수정한 부분: id, type, name, description을 따로 받지 않고 ChannelUpdateRequest DTO로 받음
    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 수정 요청은 비어 있을 수 없습니다.");
        }

        Channel channel = channelRepository.findById(request.getId());

        if (channel == null) {
            throw new IllegalArgumentException("수정할 채널을 찾을 수 없습니다.");
        }

        channel.update(
                request.getType(),
                request.getName(),
                request.getDescription()
        );

        channelRepository.save(channel);

        return toResponse(channel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }

        channelRepository.deleteById(id);
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