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

public class FileMessageService implements MessageService {

    private final Path filePath;

    private final UserService userService;
    private final ChannelService channelService;

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
    // 수정한 부분: content, authorId, channelId를 따로 받지 않고 MessageCreateRequest DTO를 받음
    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 생성 요청은 비어 있을 수 없습니다.");
        }

        validateContent(request.getContent());

        Map<UUID, Message> data = loadData();

        // 작성자와 채널이 실제 존재하는지 확인
        // read()에서 없으면 예외가 발생하므로 검증 용도로 사용 가능
        userService.read(request.getAuthorId());
        channelService.read(request.getChannelId());

        Message message = new Message(
                request.getContent(),
                request.getAuthorId(),
                request.getChannelId()
        );

        data.put(message.getId(), message);
        saveData(data);

        return toResponse(message);
    }

    // 메시지 단건 조회
    // 수정한 부분: Message 엔티티가 아니라 MessageResponse 반환
    @Override
    public MessageResponse read(UUID id) {
        Map<UUID, Message> data = loadData();

        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("조회할 메시지를 찾을 수 없습니다.");
        }

        return toResponse(message);
    }

    // 전체 메시지 조회
    // 수정한 부분: List<Message>가 아니라 List<MessageResponse> 반환
    @Override
    public List<MessageResponse> readAll() {
        Map<UUID, Message> data = loadData();

        List<MessageResponse> responses = new ArrayList<>();

        for (Message message : data.values()) {
            responses.add(toResponse(message));
        }

        return responses;
    }

    // 메시지 수정
    // 수정한 부분: id, content를 따로 받지 않고 MessageUpdateRequest DTO를 받음
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