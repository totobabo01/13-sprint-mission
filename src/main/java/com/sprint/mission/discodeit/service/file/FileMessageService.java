package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 파일 기반 MessageService 구현체
// 주의: 현재 고도화된 구조에서는 BasicMessageService + FileMessageRepository 사용을 권장함
// 이 클래스는 기존 파일 서비스 구조를 유지하면서 컴파일 에러를 없애기 위해 수정한 버전
public class FileMessageService implements MessageService {

    // 메시지 데이터를 저장할 파일 경로
    private final Path filePath;

    // 작성자 존재 여부를 확인하기 위한 UserService
    private final UserService userService;

    // 채널 존재 여부를 확인하기 위한 ChannelService
    private final ChannelService channelService;

    // 생성자
    public FileMessageService(Path filePath, UserService userService, ChannelService channelService) {
        this.filePath = filePath;
        this.userService = userService;
        this.channelService = channelService;
    }

    // 파일에서 메시지 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // 메시지 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, Message> data) {
        try {
            // 부모 폴더가 있는 경우에만 생성
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 메시지 생성
    // MessageCreateRequest DTO를 받아 Message를 생성함
    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 생성 요청은 비어 있을 수 없습니다.");
        }

        validateContent(request.getContent());

        Map<UUID, Message> data = loadData();

        // 작성자가 실제 존재하는지 확인
        userService.read(request.getAuthorId());

        // 채널이 실제 존재하는지 확인
        // ChannelService는 read()가 아니라 find() 사용
        channelService.find(request.getChannelId());

        Message message = new Message(
                request.getContent(),
                request.getAuthorId(),
                request.getChannelId()
        );

        // 주의:
        // 이 FileMessageService는 기존 구조 유지용이라 첨부파일 BinaryContent 저장은 처리하지 않음
        // 첨부파일까지 정상 처리하려면 BasicMessageService + BinaryContentRepository 구조를 사용해야 함

        data.put(message.getId(), message);
        saveData(data);

        return toResponse(message);
    }

    // 메시지 단건 조회
    @Override
    public MessageResponse read(UUID id) {
        Map<UUID, Message> data = loadData();

        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("조회할 메시지를 찾을 수 없습니다.");
        }

        return toResponse(message);
    }

    // 수정한 부분:
    // 기존 readAll() 제거
    // MessageService 인터페이스 변경에 맞춰 특정 채널의 메시지만 조회하도록 수정
    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        // 채널이 실제 존재하는지 확인
        channelService.find(channelId);

        Map<UUID, Message> data = loadData();

        List<MessageResponse> responses = new ArrayList<>();

        for (Message message : data.values()) {
            if (message.getChannelId().equals(channelId)) {
                responses.add(toResponse(message));
            }
        }

        return responses;
    }

    // 메시지 수정
    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 수정 요청은 비어 있을 수 없습니다.");
        }

        validateContent(request.getContent());

        Map<UUID, Message> data = loadData();

        Message message = data.get(request.getId());

        if (message == null) {
            throw new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다.");
        }

        message.update(request.getContent());
        saveData(data);

        return toResponse(message);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = loadData();

        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }

        data.remove(id);
        saveData(data);
    }

    // 메시지 내용 검증
    // null, 빈 문자열, 공백만 있는 메시지를 방지
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
    }

    // Message 엔티티를 MessageResponse DTO로 변환하는 보조 메서드
    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getAttachmentIds()
        );
    }
}