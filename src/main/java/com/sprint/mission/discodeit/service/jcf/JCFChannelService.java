package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// JCF 기반 ChannelService 구현체
// 주의: 현재 고도화된 구조에서는 BasicChannelService + Repository 사용을 권장함
// 이 클래스는 기존 JCF 서비스 구조를 유지하면서 컴파일 에러를 없애기 위해 수정한 버전
public class JCFChannelService implements ChannelService {

    // 채널 데이터를 메모리에 저장하는 Map
    // key: Channel id
    // value: Channel 객체
    private final Map<UUID, Channel> data;

    // PRIVATE 채널 참여자 목록을 저장하는 Map
    // key: Channel id
    // value: 참여자 User id 목록
    // 원래 고도화 구조에서는 ReadStatusRepository가 이 역할을 하지만,
    // 이 JCFChannelService는 Repository를 사용하지 않으므로 별도 Map으로 관리
    private final Map<UUID, List<UUID>> privateChannelParticipants;

    // 생성자
    public JCFChannelService() {
        this.data = new HashMap<>();
        this.privateChannelParticipants = new HashMap<>();
    }

    // PUBLIC 채널 생성
    // 수정한 부분: 기존 create() 대신 createPublicChannel() 구현
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

        UUID id = channel.getId();
        data.put(id, channel);

        return toResponse(channel);
    }

    // PRIVATE 채널 생성
    // 수정한 부분: 새 ChannelService 인터페이스에 맞춰 추가
    // PRIVATE 채널은 name, description 없이 참여자 목록만으로 생성
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PRIVATE 채널 생성 요청은 비어 있을 수 없습니다.");
        }

        List<UUID> participantUserIds = request.getParticipantUserIds();

        if (participantUserIds == null || participantUserIds.isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널 참여자 목록은 비어 있을 수 없습니다.");
        }

        // 중복 참여자 검사용 Map
        List<UUID> uniqueParticipantUserIds = new ArrayList<>();

        for (UUID userId : participantUserIds) {
            if (userId == null) {
                throw new IllegalArgumentException("참여자 id는 null일 수 없습니다.");
            }

            if (uniqueParticipantUserIds.contains(userId)) {
                throw new IllegalArgumentException("PRIVATE 채널 참여자 id가 중복되었습니다.");
            }

            uniqueParticipantUserIds.add(userId);
        }

        // PRIVATE 채널 생성
        // PRIVATE 채널은 name, description을 생략하므로 null로 저장
        // 만약 Channel 엔티티에서 null을 허용하지 않으면 Channel.java 수정이 필요함
        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );

        UUID id = channel.getId();
        data.put(id, channel);

        // PRIVATE 채널 참여자 목록 저장
        privateChannelParticipants.put(id, uniqueParticipantUserIds);

        return toResponse(channel);
    }

    // 채널 단건 조회
    // 수정한 부분: 기존 read() 대신 find() 구현
    @Override
    public ChannelResponse find(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널 id입니다.");
        }

        return toResponse(channel);
    }

    // 특정 User가 볼 수 있는 채널 목록 조회
    // 수정한 부분: 기존 readAll() 대신 findAllByUserId() 구현
    // PUBLIC 채널은 모두 조회 가능
    // PRIVATE 채널은 해당 User가 참여자 목록에 있을 때만 조회 가능
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 id는 null일 수 없습니다.");
        }

        List<ChannelResponse> result = new ArrayList<>();

        for (Channel channel : data.values()) {
            if (channel.getType() == ChannelType.PUBLIC) {
                result.add(toResponse(channel));
                continue;
            }

            List<UUID> participantUserIds = privateChannelParticipants.get(channel.getId());

            if (participantUserIds != null && participantUserIds.contains(userId)) {
                result.add(toResponse(channel));
            }
        }

        return result;
    }

    // 채널 수정
    // PUBLIC 채널만 수정 가능
    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 수정 요청은 비어 있을 수 없습니다.");
        }

        Channel channel = data.get(request.getId());

        if (channel == null) {
            throw new IllegalArgumentException("수정할 채널을 찾을 수 없습니다.");
        }

        // PRIVATE 채널은 수정 불가
        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("PRIVATE 채널은 수정할 수 없습니다.");
        }

        // PUBLIC 채널은 PUBLIC 상태로 유지하면서 name, description만 수정
        channel.update(
                ChannelType.PUBLIC,
                request.getName(),
                request.getDescription()
        );

        return toResponse(channel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }

        data.remove(id);

        // PRIVATE 채널 참여자 정보도 같이 삭제
        privateChannelParticipants.remove(id);
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

                // JCFChannelService는 MessageRepository를 가지고 있지 않아서
                // 최근 메시지 시간을 계산할 수 없음
                null,

                // PRIVATE 채널이면 참여자 목록 반환
                // PUBLIC 채널이면 빈 리스트 반환
                getParticipantUserIds(channel)
        );
    }

    // PRIVATE 채널 참여자 id 목록을 반환하는 메서드
    private List<UUID> getParticipantUserIds(Channel channel) {
        if (channel.getType() != ChannelType.PRIVATE) {
            return new ArrayList<>();
        }

        List<UUID> participantUserIds = privateChannelParticipants.get(channel.getId());

        if (participantUserIds == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(participantUserIds);
    }
}