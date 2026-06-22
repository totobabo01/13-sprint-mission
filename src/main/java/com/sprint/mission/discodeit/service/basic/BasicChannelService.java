package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// Channel 기능을 실제로 구현하는 Service 클래스
// ChannelService 인터페이스의 기능들을 구현함
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    // PUBLIC 채널 생성
    @Override
    public ChannelResponse createPublicChannel(ChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 생성 요청은 비어 있을 수 없습니다.");
        }

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
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PRIVATE 채널 생성 요청은 비어 있을 수 없습니다.");
        }

        List<UUID> participantUserIds = request.getParticipantUserIds();

        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널 참여자 목록은 비어 있을 수 없습니다.");
        }

        Set<UUID> uniqueParticipantUserIds = new HashSet<>();

        for (UUID userId : participantUserIds) {
            if (userId == null) {
                throw new IllegalArgumentException("참여자 id는 null일 수 없습니다.");
            }

            if (!uniqueParticipantUserIds.add(userId)) {
                throw new IllegalArgumentException("PRIVATE 채널 참여자 id가 중복되었습니다.");
            }
        }

        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );

        channelRepository.save(channel);

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
    @Override
    public ChannelResponse find(UUID id) {
        Channel channel = findChannelById(id);

        if (channel == null) {
            throw new IllegalArgumentException("조회할 채널을 찾을 수 없습니다. id=" + id);
        }

        return toResponse(channel);
    }

    // 특정 User가 볼 수 있는 채널 목록 조회
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (!existsUserById(userId)) {
            throw new IllegalArgumentException("채널을 조회할 사용자를 찾을 수 없습니다. userId=" + userId);
        }

        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponse> responses = new ArrayList<>();

        Set<UUID> participatedPrivateChannelIds =
                new HashSet<>(readStatusRepository.findChannelIdsByUserId(userId));

        for (Channel channel : channels) {
            if (channel.getType() == ChannelType.PUBLIC) {
                responses.add(toResponse(channel));
                continue;
            }

            if (channel.getType() == ChannelType.PRIVATE
                    && participatedPrivateChannelIds.contains(channel.getId())) {
                responses.add(toResponse(channel));
            }
        }

        return responses;
    }

    // 채널 수정
    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 수정 요청은 비어 있을 수 없습니다.");
        }

        if (request.getId() == null) {
            throw new IllegalArgumentException("수정할 채널 id는 null일 수 없습니다.");
        }

        Channel channel = findChannelById(request.getId());

        if (channel == null) {
            throw new IllegalArgumentException("수정할 채널을 찾을 수 없습니다. id=" + request.getId());
        }

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        channel.update(
                ChannelType.PUBLIC,
                request.getName(),
                request.getDescription()
        );

        channelRepository.save(channel);

        return toResponse(channel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        Channel channel = findChannelById(id);

        if (channel == null) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다. id=" + id);
        }

        messageRepository.deleteByChannelId(id);
        readStatusRepository.deleteByChannelId(id);
        channelRepository.deleteById(id);
    }

    // User 존재 여부 확인
    // FileRepository의 existsById가 꼬이는 상황을 피하기 위해 findAll 기준으로 직접 비교
    private boolean existsUserById(UUID userId) {
        if (userId == null) {
            return false;
        }

        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getId() != null && user.getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }

    // Channel 단건 조회 보조 메서드
    // channelRepository.findById가 못 찾는 상황을 피하기 위해 findAll 기준으로 직접 비교
    private Channel findChannelById(UUID channelId) {
        if (channelId == null) {
            return null;
        }

        List<Channel> channels = channelRepository.findAll();

        for (Channel channel : channels) {
            if (channel.getId() != null && channel.getId().equals(channelId)) {
                return channel;
            }
        }

        return null;
    }

    // Channel 엔티티를 ChannelResponse DTO로 변환
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

    private Instant getLastMessageAt(UUID channelId) {
        return messageRepository.findLastMessageAtByChannelId(channelId);
    }

    private List<UUID> getParticipantUserIds(Channel channel) {
        if (channel.getType() != ChannelType.PRIVATE) {
            return new ArrayList<>();
        }

        return readStatusRepository.findUserIdsByChannelId(channel.getId());
    }
}