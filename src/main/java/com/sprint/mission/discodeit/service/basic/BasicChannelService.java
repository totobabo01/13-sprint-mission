package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// Channel 기능을 실제로 구현하는 Service 클래스
// ChannelService 인터페이스의 기능들을 구현함
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    // Channel 데이터를 저장하고 조회하기 위한 Repository
    private final ChannelRepository channelRepository;

    // User 존재 여부를 확인하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 UserService 대신 UserRepository 사용
    private final UserRepository userRepository;

    // Message 데이터를 조회/삭제하기 위한 Repository
    // 채널의 최근 메시지 시간 조회, 채널 삭제 시 관련 메시지 삭제에 사용
    private final MessageRepository messageRepository;

    // ReadStatus 데이터를 생성/조회/삭제하기 위한 Repository
    // PRIVATE 채널 참여자 관리, 채널 삭제 시 관련 ReadStatus 삭제에 사용
    private final ReadStatusRepository readStatusRepository;

    // PUBLIC 채널 생성
    // PUBLIC 채널은 name, description을 가질 수 있음
    @Override
    public ChannelResponse createPublicChannel(ChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 생성 요청은 비어 있을 수 없습니다.");
        }

        // PUBLIC 채널 생성 메서드이므로 type은 PUBLIC이어야 함
        if (request.getType() != ChannelType.PUBLIC) {
            throw new IllegalArgumentException("PUBLIC 채널 생성 요청만 처리할 수 있습니다.");
        }

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.getName(),
                request.getDescription()
        );

        channelRepository.save(channel);

        return toResponse(channel);
    }

    // PRIVATE 채널 생성
    // PRIVATE 채널은 name, description을 생략하고 참여자 목록을 기준으로 생성함
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PRIVATE 채널 생성 요청은 비어 있을 수 없습니다.");
        }

        List<UUID> participantUserIds = request.getParticipantUserIds();

        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널 참여자 목록은 비어 있을 수 없습니다.");
        }

        // 중복 참여자 id 검사용 Set
        Set<UUID> uniqueParticipantUserIds = new HashSet<>();

        for (UUID userId : participantUserIds) {
            if (userId == null) {
                throw new IllegalArgumentException("참여자 id는 null일 수 없습니다.");
            }

            if (!userRepository.existsById(userId)) {
                throw new IllegalArgumentException("PRIVATE 채널 참여자를 찾을 수 없습니다.");
            }

            if (!uniqueParticipantUserIds.add(userId)) {
                throw new IllegalArgumentException("PRIVATE 채널 참여자 id가 중복되었습니다.");
            }
        }

        // PRIVATE 채널 생성
        // PRIVATE 채널은 name, description을 생략하므로 null로 저장
        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );

        channelRepository.save(channel);

        // PRIVATE 채널 참여자마다 ReadStatus 생성
        // ReadStatus가 있어야 나중에 해당 유저가 참여한 PRIVATE 채널을 찾을 수 있음
        for (UUID userId : participantUserIds) {
            ReadStatus readStatus = new ReadStatus(
                    userId,
                    channel.getId()
            );

            readStatusRepository.save(readStatus);
        }

        return toResponse(channel);
    }

    // id로 채널 단건 조회
    // 기존 read 이름을 find로 변경
    @Override
    public ChannelResponse find(UUID id) {
        Channel channel = channelRepository.findById(id);

        if (channel == null) {
            throw new IllegalArgumentException("조회할 채널을 찾을 수 없습니다.");
        }

        return toResponse(channel);
    }

    // 특정 User가 볼 수 있는 채널 목록 조회
    // PUBLIC 채널은 모두 조회 가능
    // PRIVATE 채널은 해당 User가 참여한 채널만 조회 가능
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("채널을 조회할 사용자를 찾을 수 없습니다.");
        }

        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponse> responses = new ArrayList<>();

        // 해당 User가 참여한 PRIVATE 채널 id 목록
        Set<UUID> participatedPrivateChannelIds = new HashSet<>();

        List<ReadStatus> userReadStatuses = readStatusRepository.findAllByUserId(userId);

        for (ReadStatus readStatus : userReadStatuses) {
            participatedPrivateChannelIds.add(readStatus.getChannelId());
        }

        for (Channel channel : channels) {
            // PUBLIC 채널은 모든 사용자가 조회 가능
            if (channel.getType() == ChannelType.PUBLIC) {
                responses.add(toResponse(channel));
                continue;
            }

            // PRIVATE 채널은 참여한 사용자만 조회 가능
            if (channel.getType() == ChannelType.PRIVATE
                    && participatedPrivateChannelIds.contains(channel.getId())) {
                responses.add(toResponse(channel));
            }
        }

        return responses;
    }

    // 채널 수정
    // PUBLIC 채널만 수정 가능
    // PRIVATE 채널은 수정할 수 없음
    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 수정 요청은 비어 있을 수 없습니다.");
        }

        Channel channel = channelRepository.findById(request.getId());

        if (channel == null) {
            throw new IllegalArgumentException("수정할 채널을 찾을 수 없습니다.");
        }

        // PRIVATE 채널은 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // PUBLIC 채널 수정 시 type은 PUBLIC으로 유지
        channel.update(
                ChannelType.PUBLIC,
                request.getName(),
                request.getDescription()
        );

        channelRepository.save(channel);

        return toResponse(channel);
    }

    // 채널 삭제
    // 채널 삭제 시 해당 채널의 Message, ReadStatus도 같이 삭제
    @Override
    public void delete(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }

        // 해당 채널의 메시지 삭제
        messageRepository.deleteByChannelId(id);

        // 해당 채널의 읽음 상태 삭제
        readStatusRepository.deleteByChannelId(id);

        // 채널 삭제
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
                channel.getDescription(),
                getLastMessageAt(channel.getId()),
                getParticipantUserIds(channel)
        );
    }

    // 해당 채널의 가장 최근 메시지 생성 시간을 구하는 메서드
    // 메시지가 없으면 null 반환
    private Instant getLastMessageAt(UUID channelId) {
        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        Instant lastMessageAt = null;

        for (Message message : messages) {
            if (lastMessageAt == null || message.getCreatedAt().isAfter(lastMessageAt)) {
                lastMessageAt = message.getCreatedAt();
            }
        }

        return lastMessageAt;
    }

    // PRIVATE 채널의 참여자 id 목록을 구하는 메서드
    // PUBLIC 채널이면 빈 리스트 반환
    private List<UUID> getParticipantUserIds(Channel channel) {
        List<UUID> participantUserIds = new ArrayList<>();

        if (channel.getType() != ChannelType.PRIVATE) {
            return participantUserIds;
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());

        for (ReadStatus readStatus : readStatuses) {
            participantUserIds.add(readStatus.getUserId());
        }

        return participantUserIds;
    }
}