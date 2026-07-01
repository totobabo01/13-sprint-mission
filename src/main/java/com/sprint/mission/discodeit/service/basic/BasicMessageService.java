package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 메시지 생성
    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 생성 요청은 비어 있을 수 없습니다.");
        }

        String content = request.getContent();
        UUID authorId = request.getAuthorId();
        UUID channelId = request.getChannelId();

        // 수정됨: content 검증
        validateContent(content);

        // 수정됨: authorId null 방어
        if (authorId == null) {
            throw new IllegalArgumentException("메시지 작성자 id는 필수입니다.");
        }

        // 수정됨: channelId null 방어
        if (channelId == null) {
            throw new IllegalArgumentException("메시지를 작성할 채널 id는 필수입니다.");
        }

        // 수정됨: 실제 없는 사용자 id면 명확하게 예외 출력
        if (!userRepository.existsById(authorId)) {
            throw new IllegalArgumentException("메시지를 작성할 사용자를 찾을 수 없습니다. authorId=" + authorId);
        }

        // 수정됨: 실제 없는 채널 id면 명확하게 예외 출력
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("메시지를 작성할 채널을 찾을 수 없습니다. channelId=" + channelId);
        }

        Message message = new Message(
                content,
                authorId,
                channelId
        );

        List<BinaryContentCreateRequest> attachments = request.getAttachments();

        // 수정됨: attachments가 null이면 그냥 첨부파일 없는 메시지로 처리
        if (attachments != null && !attachments.isEmpty()) {
            for (BinaryContentCreateRequest attachmentRequest : attachments) {
                if (attachmentRequest == null) {
                    continue;
                }

                validateAttachment(attachmentRequest);

                BinaryContent binaryContent = new BinaryContent(
                        attachmentRequest.getFileName(),
                        attachmentRequest.getContentType(),
                        attachmentRequest.getBytes()
                );

                binaryContentRepository.save(binaryContent);

                // 수정됨: attachmentIds가 null이면 NPE 방지
                if (message.getAttachmentIds() != null) {
                    message.getAttachmentIds().add(binaryContent.getId());
                }
            }
        }

        messageRepository.save(message);

        return toResponse(message);
    }

    // 메시지 단건 조회
    @Override
    public MessageResponse read(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 메시지 id는 필수입니다.");
        }

        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("조회할 메시지를 찾을 수 없습니다. id=" + id);
        }

        return toResponse(message);
    }

    // 특정 Channel에 작성된 메시지 목록 조회
    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("메시지를 조회할 채널 id는 필수입니다.");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("메시지를 조회할 채널을 찾을 수 없습니다. channelId=" + channelId);
        }

        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        List<MessageResponse> responses = new ArrayList<>();

        for (Message message : messages) {
            responses.add(toResponse(message));
        }

        return responses;
    }

    // 메시지 수정
    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("메시지 수정 요청은 비어 있을 수 없습니다.");
        }

        if (request.getId() == null) {
            throw new IllegalArgumentException("수정할 메시지 id는 필수입니다.");
        }

        validateContent(request.getContent());

        Message message = messageRepository.findById(request.getId());

        if (message == null) {
            throw new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다. id=" + request.getId());
        }

        message.update(request.getContent());
        messageRepository.save(message);

        return toResponse(message);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("삭제할 메시지 id는 필수입니다.");
        }

        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다. id=" + id);
        }

        List<UUID> attachmentIds = message.getAttachmentIds();

        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (UUID attachmentId : attachmentIds) {
                if (attachmentId != null && binaryContentRepository.existsById(attachmentId)) {
                    binaryContentRepository.deleteById(attachmentId);
                }
            }
        }

        messageRepository.deleteById(id);
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
    }

    private void validateAttachment(BinaryContentCreateRequest request) {
        if (request.getFileName() == null || request.getFileName().isBlank()) {
            throw new IllegalArgumentException("첨부파일 이름은 비어 있을 수 없습니다.");
        }

        if (request.getContentType() == null || request.getContentType().isBlank()) {
            throw new IllegalArgumentException("첨부파일 타입은 비어 있을 수 없습니다.");
        }

        if (request.getBytes() == null || request.getBytes().length == 0) {
            throw new IllegalArgumentException("첨부파일 데이터는 비어 있을 수 없습니다.");
        }
    }

    private MessageResponse toResponse(Message message) {
        List<UUID> attachmentIds = message.getAttachmentIds();

        // 수정됨: 응답에서 attachmentIds가 null이면 빈 리스트로 반환
        if (attachmentIds == null) {
            attachmentIds = List.of();
        }

        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getAuthorId(),
                message.getChannelId(),
                attachmentIds
        );
    }
}