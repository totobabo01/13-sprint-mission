package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;

import java.util.List;
import java.util.UUID;

// Channel 관련 기능을 정의하는 Service 인터페이스
// 채널 생성, 조회, 수정, 삭제 기능의 규칙을 선언함
public interface ChannelService {

    // PUBLIC 채널 생성 기능
    // PUBLIC 채널은 name, description을 가질 수 있음
    // ChannelCreateRequest DTO를 통해 type, name, description 값을 받음
    ChannelResponse createPublicChannel(ChannelCreateRequest request);

    // PRIVATE 채널 생성 기능
    // PRIVATE 채널은 name, description을 생략하고 참여자 목록을 기준으로 생성함
    // PrivateChannelCreateRequest DTO 안의 participantUserIds를 사용해서
    // 참여자별 ReadStatus도 함께 생성할 예정
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);

    // id로 채널 단건 조회 기능
    // 기존 read 이름을 find로 변경
    // Channel 엔티티가 아니라 ChannelResponse DTO를 반환함
    ChannelResponse find(UUID id);

    // 특정 User가 볼 수 있는 채널 목록 조회 기능
    // PUBLIC 채널은 모두 조회 가능
    // PRIVATE 채널은 해당 User가 참여한 채널만 조회 가능
    // 기존 readAll 대신 userId 기준 조회로 변경
    List<ChannelResponse> findAllByUserId(UUID userId);

    // 채널 수정 기능
    // PUBLIC 채널만 수정 가능
    // PRIVATE 채널은 수정할 수 없도록 BasicChannelService에서 예외 처리할 예정
    ChannelResponse update(ChannelUpdateRequest request);

    // 채널 삭제 기능
    // 채널 삭제 시 해당 채널의 Message, ReadStatus도 같이 삭제할 예정
    void delete(UUID id);
}