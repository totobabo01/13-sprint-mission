package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 파일 기반 ChannelService 구현체
// 주의: 현재 고도화된 구조에서는 BasicChannelService + FileChannelRepository 사용을 권장함
// 이 클래스는 컴파일 에러를 막기 위해 ChannelService 변경 사항에 맞춰 수정한 버전
public class FileChannelService implements ChannelService {

    // 채널 데이터를 저장할 파일 경로
    private final Path filePath;

    // 생성자: 저장 파일 경로를 외부에서 주입받음
    public FileChannelService(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 채널 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // 채널 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, Channel> data) {
        try {
            // 부모 폴더가 있는 경우에만 생성
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
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

        Map<UUID, Channel> data = loadData();

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.getName(),
                request.getDescription()
        );

        data.put(channel.getId(), channel);
        saveData(data);

        return toResponse(channel);
    }

    // PRIVATE 채널 생성
    // 수정한 부분: 새 ChannelService 인터페이스에 맞춰 추가
    // 주의: 이 클래스는 ReadStatusRepository를 가지고 있지 않기 때문에
    // 참여자별 ReadStatus 생성은 처리하지 못함
    // 실제 고도화 기능은 BasicChannelService에서 처리하는 것이 맞음
    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PRIVATE 채널 생성 요청은 비어 있을 수 없습니다.");
        }

        if (request.getParticipantUserIds() == null || request.getParticipantUserIds().isEmpty()) {
            throw new IllegalArgumentException("PRIVATE 채널 참여자 목록은 비어 있을 수 없습니다.");
        }

        Map<UUID, Channel> data = loadData();

        // PRIVATE 채널은 name, description을 생략하기 위해 null로 저장
        // 만약 Channel 엔티티에서 null을 허용하지 않으면 Channel.java 수정이 필요함
        Channel channel = new Channel(
                ChannelType.PRIVATE,
                null,
                null
        );

        data.put(channel.getId(), channel);
        saveData(data);

        return new ChannelResponse(
                channel.getId(),
                channel.getCreatedAt(),
                channel.getUpdatedAt(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                null,
                request.getParticipantUserIds()
        );
    }

    // 채널 단건 조회
    // 수정한 부분: 기존 read() 대신 find() 구현
    @Override
    public ChannelResponse find(UUID id) {
        Map<UUID, Channel> data = loadData();

        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("조회할 채널을 찾을 수 없습니다.");
        }

        return toResponse(channel);
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    // 수정한 부분: 기존 readAll() 대신 findAllByUserId() 구현
    // 주의: 이 클래스는 ReadStatus 정보를 모르기 때문에
    // 정확한 PRIVATE 채널 필터링은 불가능함
    // 그래서 여기서는 저장된 모든 채널을 반환함
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 id는 null일 수 없습니다.");
        }

        Map<UUID, Channel> data = loadData();

        List<ChannelResponse> responses = new ArrayList<>();

        for (Channel channel : data.values()) {
            responses.add(toResponse(channel));
        }

        return responses;
    }

    // 채널 수정
    // PUBLIC 채널만 수정 가능
    @Override
    public ChannelResponse update(ChannelUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 수정 요청은 비어 있을 수 없습니다.");
        }

        Map<UUID, Channel> data = loadData();

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

        saveData(data);

        return toResponse(channel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = loadData();

        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }

        data.remove(id);
        saveData(data);
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

                // FileChannelService는 MessageRepository를 가지고 있지 않아서
                // 최근 메시지 시간을 계산할 수 없음
                null,

                // FileChannelService는 ReadStatusRepository를 가지고 있지 않아서
                // PRIVATE 채널 참여자 목록을 정확히 계산할 수 없음
                Collections.emptyList()
        );
    }
}