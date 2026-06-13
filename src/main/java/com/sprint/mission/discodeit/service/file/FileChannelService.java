package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final Path filePath;

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

    // 채널 생성
    // 수정한 부분: ChannelType, name, description을 따로 받지 않고 ChannelCreateRequest DTO를 받음
    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("채널 생성 요청은 비어 있을 수 없습니다.");
        }

        Map<UUID, Channel> data = loadData();

        Channel channel = new Channel(
                request.getType(),
                request.getName(),
                request.getDescription()
        );

        data.put(channel.getId(), channel);
        saveData(data);

        return toResponse(channel);
    }

    // 채널 단건 조회
    // 수정한 부분: Channel 엔티티가 아니라 ChannelResponse 반환
    @Override
    public ChannelResponse read(UUID id) {
        Map<UUID, Channel> data = loadData();

        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("조회할 채널을 찾을 수 없습니다.");
        }

        return toResponse(channel);
    }

    // 전체 채널 조회
    // 수정한 부분: List<Channel>이 아니라 List<ChannelResponse> 반환
    @Override
    public List<ChannelResponse> readAll() {
        Map<UUID, Channel> data = loadData();

        List<ChannelResponse> responses = new ArrayList<>();

        for (Channel channel : data.values()) {
            responses.add(toResponse(channel));
        }

        return responses;
    }

    // 채널 수정
    // 수정한 부분: id, type, name, description을 따로 받지 않고 ChannelUpdateRequest DTO를 받음
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

        channel.update(
                request.getType(),
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
                channel.getDescription()
        );
    }
}