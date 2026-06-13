package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    // 메시지 데이터를 메모리에 저장하는 Map
    private final Map<UUID, Message> data;

    // 작성자 존재 여부 확인용 UserService
    private final UserService userService;

    // 채널 존재 여부 확인용 ChannelService
    private final ChannelService channelService;

    // 생성자
    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    // 메시지 생성
    // 수정한 부분: content, authorId, channelId를 따로 받지 않고 MessageCreateRequest DTO를 받음
    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 생성 요청은 비어 있을 수 없습니다.");
        }

        validateContent(request.getContent());

        // 작성자와 채널이 존재하는지 확인
        // read()에서 없으면 예외가 발생하므로 검증 역할을 함
        UserResponse author = userService.read(request.getAuthorId());
        ChannelResponse channel = channelService.read(request.getChannelId());

        if (author == null || channel == null) {
            throw new IllegalArgumentException("메시지를 생성할 작성자 또는 채널을 찾을 수 없습니다.");
        }

        Message message = new Message(
                request.getContent(),
                request.getAuthorId(),
                request.getChannelId()
        );

        data.put(message.getId(), message);

        return toResponse(message);
    }

    // 메시지 단건 조회
    // 수정한 부분: Message 엔티티가 아니라 MessageResponse 반환
    @Override
    public MessageResponse read(UUID id) {
        Message message = data.get(id);

        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지 id입니다.");
        }

        return toResponse(message);
    }

    // 메시지 전체 조회
    // 수정한 부분: List<Message>가 아니라 List<MessageResponse> 반환
    @Override
    public List<MessageResponse> readAll() {
        List<MessageResponse> allMessages = new ArrayList<>();

        for (Message message : data.values()) {
            allMessages.add(toResponse(message));
        }

        return allMessages;
    }

    // 메시지 수정
    // 수정한 부분: id, content를 따로 받지 않고 MessageUpdateRequest DTO를 받음
    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 수정 요청은 비어 있을 수 없습니다.");
        }

        validateContent(request.getContent());

        Message message = data.get(request.getId());

        if (message == null) {
            throw new IllegalArgumentException("수정된 메시지 정보를 조회할 수 없습니다.");
        }

        message.update(request.getContent());

        return toResponse(message);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        if (data.get(id) == null) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }

        data.remove(id);
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