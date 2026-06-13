package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    // 메시지 생성
    // 수정한 부분: content, authorId, channelId를 따로 받지 않고 MessageCreateRequest DTO로 받음
    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 생성 요청은 비어 있을 수 없습니다.");
        }

        // 메시지 생성 전에 내용이 비어 있거나 공백인지 검증
        validateContent(request.getContent());

        // 작성자 id가 실제 존재하는 User인지 확인
        if (!userRepository.existsById(request.getAuthorId())) {
            throw new IllegalArgumentException("메시지를 작성할 사용자를 찾을 수 없습니다.");
        }

        // 채널 id가 실제 존재하는 Channel인지 확인
        if (!channelRepository.existsById(request.getChannelId())) {
            throw new IllegalArgumentException("메시지를 작성할 채널을 찾을 수 없습니다.");
        }

        // DTO에서 값을 꺼내 Message 엔티티 생성
        Message message = new Message(
                request.getContent(),
                request.getAuthorId(),
                request.getChannelId()
        );

        messageRepository.save(message);

        // Message 엔티티를 그대로 반환하지 않고 MessageResponse로 변환해서 반환
        return toResponse(message);
    }

    // 메시지 단건 조회
    // 수정한 부분: Message 엔티티가 아니라 MessageResponse 반환
    @Override
    public MessageResponse read(UUID id) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("조회할 메시지를 찾을 수 없습니다.");
        }

        return toResponse(message);
    }

    // 메시지 전체 조회
    // 수정한 부분: List<Message>가 아니라 List<MessageResponse> 반환
    @Override
    public List<MessageResponse> readAll() {
        List<Message> messages = messageRepository.findAll();
        List<MessageResponse> responses = new ArrayList<>();

        for (Message message : messages) {
            responses.add(toResponse(message));
        }

        return responses;
    }

    // 메시지 수정
    // 수정한 부분: id, content를 따로 받지 않고 MessageUpdateRequest DTO로 받음
    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 수정 요청은 비어 있을 수 없습니다.");
        }

        // 메시지 수정 전에도 내용이 비어 있거나 공백인지 검증
        validateContent(request.getContent());

        Message message = messageRepository.findById(request.getId());

        if (message == null) {
            throw new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다.");
        }

        message.update(request.getContent());
        messageRepository.save(message);

        return toResponse(message);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        if (!messageRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }

        messageRepository.deleteById(id);
    }

    // 메시지 내용 검증 로직
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