package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
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

        validateContent(content);

        if (authorId == null) {
            throw new IllegalArgumentException("메시지 작성자 id는 필수입니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("메시지를 작성할 채널 id는 필수입니다.");
        }

        if (!userRepository.existsById(authorId)) {
            throw new IllegalArgumentException("메시지를 작성할 사용자를 찾을 수 없습니다. authorId=" + authorId);
        }

        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("메시지를 작성할 채널을 찾을 수 없습니다. channelId=" + channelId);
        }

        Message message = new Message(
                content,
                authorId,
                channelId
        );

        List<BinaryContentCreateRequest> attachments = request.getAttachments();

        if (attachments != null && !attachments.isEmpty()) {
            for (BinaryContentCreateRequest attachmentRequest : attachments) {
                if (attachmentRequest == null) {
                    continue;
                }

                validateAttachment(attachmentRequest);

                BinaryContent binaryContent = new BinaryContent(
                        attachmentRequest.getFileName(),
                        safeContentType(attachmentRequest.getContentType()),
                        attachmentRequest.getBytes()
                );

                BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

                message.addAttachment(savedBinaryContent.getId());
            }
        }

        Message savedMessage = messageRepository.save(message);

        return toResponse(savedMessage);
    }

    // 메시지 단건 조회
    @Override
    @Transactional(readOnly = true)
    public MessageResponse read(UUID id) {
        Message message = findMessageById(id);

        return toResponse(message);
    }

    // 특정 Channel에 작성된 메시지 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("메시지를 조회할 채널 id는 필수입니다.");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("메시지를 조회할 채널을 찾을 수 없습니다. channelId=" + channelId);
        }

        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        messages.sort(
                Comparator.comparing(Message::getCreatedAt)
                        .reversed()
        );

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

        Message message = findMessageById(request.getId());

        message.update(request.getContent());

        Message savedMessage = messageRepository.save(message);

        return toResponse(savedMessage);
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        Message message = findMessageById(id);

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

    private Message findMessageById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("메시지 id는 필수입니다.");
        }

        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "메시지를 찾을 수 없습니다. id=" + id
                ));
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

        if (request.getBytes() == null || request.getBytes().length == 0) {
            throw new IllegalArgumentException("첨부파일 데이터는 비어 있을 수 없습니다.");
        }
    }

    private MessageResponse toResponse(Message message) {
        List<UUID> attachmentIds = new ArrayList<>();

        if (message.getAttachmentIds() != null) {
            attachmentIds.addAll(message.getAttachmentIds());
        }

        UserResponse authorResponse = toAuthorResponse(message.getAuthorId());

        List<BinaryContentResponse> attachments = toAttachmentResponses(attachmentIds);

        return new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getAuthorId(),
                authorResponse,
                message.getChannelId(),
                attachmentIds,
                attachments
        );
    }

    private List<BinaryContentResponse> toAttachmentResponses(List<UUID> attachmentIds) {
        List<BinaryContentResponse> attachments = new ArrayList<>();

        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return attachments;
        }

        for (UUID attachmentId : attachmentIds) {
            if (attachmentId == null) {
                continue;
            }

            binaryContentRepository.findById(attachmentId)
                    .map(this::toBinaryContentResponse)
                    .ifPresent(attachments::add);
        }

        return attachments;
    }

    private BinaryContentResponse toBinaryContentResponse(BinaryContent binaryContent) {
        String contentType = safeContentType(binaryContent.getContentType());

        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getUpdatedAt(),
                binaryContent.getFileName(),
                contentType,
                binaryContent.getSize()
        );
    }

    private UserResponse toAuthorResponse(UUID authorId) {
        if (authorId == null) {
            return null;
        }

        User author = userRepository.findById(authorId)
                .orElse(null);

        if (author == null) {
            return null;
        }

        BinaryContentResponse profileResponse = toProfileResponse(author.getProfileId());

        return new UserResponse(
                author.getId(),
                author.getCreatedAt(),
                author.getUpdatedAt(),
                author.getUsername(),
                author.getEmail(),
                author.getProfileId(),
                profileResponse,
                false
        );
    }

    private BinaryContentResponse toProfileResponse(UUID profileId) {
        if (profileId == null) {
            return null;
        }

        return binaryContentRepository.findById(profileId)
                .map(this::toBinaryContentResponse)
                .orElse(null);
    }

    private String safeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "application/octet-stream";
        }

        return contentType;
    }
}