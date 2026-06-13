package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // 채널 생성 기능
    // 수정한 부분: ChannelType, name, description을 따로 받지 않고 ChannelCreateRequest DTO로 받음
    ChannelResponse create(ChannelCreateRequest request);

    // 한 채널 읽기 기능
    // 수정한 부분: Channel 엔티티가 아니라 ChannelResponse DTO 반환
    ChannelResponse read(UUID id);

    // 전체 채널 읽기 기능
    // 수정한 부분: List<Channel>이 아니라 List<ChannelResponse> 반환
    List<ChannelResponse> readAll();

    // 채널 수정 기능
    // 수정한 부분: id, type, name, description을 따로 받지 않고 ChannelUpdateRequest DTO로 받음
    ChannelResponse update(ChannelUpdateRequest request);

    // 채널 삭제 기능
    void delete(UUID id);
}