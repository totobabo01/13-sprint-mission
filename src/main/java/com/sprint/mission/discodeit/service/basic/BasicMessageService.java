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

// Message 기능을 실제로 구현하는 Service 클래스
// MessageService 인터페이스의 기능들을 구현함
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    // Message 데이터를 저장하고 조회하기 위한 Repository
    private final MessageRepository messageRepository;

    // User 존재 여부를 확인하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 UserService 대신 UserRepository 사용
    private final UserRepository userRepository;

    // Channel 존재 여부를 확인하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 ChannelService 대신 ChannelRepository 사용
    private final ChannelRepository channelRepository;

    // 추가한 부분:
    // 메시지 첨부파일을 저장하고 삭제하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 BinaryContentService 대신 Repository 사용
    private final BinaryContentRepository binaryContentRepository;

    // 메시지 생성
    // MessageCreateRequest DTO를 받아 메시지를 생성함
    // 첨부파일이 있으면 BinaryContent로 저장하고 Message의 attachmentIds에 추가함
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

        // Message 엔티티 생성
        Message message = new Message(
                request.getContent(),
                request.getAuthorId(),
                request.getChannelId()
        );

        // 추가한 부분:
        // 요청에 첨부파일이 있으면 BinaryContent로 저장하고
        // 저장된 BinaryContent의 id를 Message의 attachmentIds에 추가
        List<BinaryContentCreateRequest> attachments = request.getAttachments();

        if (attachments != null && !attachments.isEmpty()) {
            for (BinaryContentCreateRequest attachmentRequest : attachments) {
                validateAttachment(attachmentRequest);

                BinaryContent binaryContent = new BinaryContent(
                        attachmentRequest.getFileName(),
                        attachmentRequest.getContentType(),
                        attachmentRequest.getBytes()
                );

                binaryContentRepository.save(binaryContent);

                // Message 엔티티의 attachmentIds에 첨부파일 id 추가
                message.getAttachmentIds().add(binaryContent.getId());
            }
        }

        messageRepository.save(message);

        // Message 엔티티를 그대로 반환하지 않고 MessageResponse로 변환해서 반환
        return toResponse(message);
    }

    // 메시지 단건 조회
    @Override
    public MessageResponse read(UUID id) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("조회할 메시지를 찾을 수 없습니다.");
        }

        return toResponse(message);
    }

    // 특정 Channel에 작성된 메시지 목록 조회
    // 기존 readAll() 대신 channelId 기준 조회로 변경
    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("메시지를 조회할 채널을 찾을 수 없습니다.");
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

        System.out.println("===== 메시지 수정 요청 확인 =====");
        System.out.println("request.getId() = " + request.getId());
        System.out.println("request.getContent() = " + request.getContent());

        System.out.println("===== 현재 저장된 메시지 목록 =====");
        for (Message savedMessage : messageRepository.findAll()) {
            System.out.println("savedMessage.getId() = " + savedMessage.getId());
        }

        if (request.getId() == null) {
            throw new IllegalArgumentException("수정할 메시지 id는 null일 수 없습니다.");
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
    // 메시지 삭제 시 해당 메시지의 첨부파일도 함께 삭제
    @Override
    public void delete(UUID id) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }

        // 추가한 부분:
        // 메시지에 연결된 첨부파일 삭제
        List<UUID> attachmentIds = message.getAttachmentIds();

        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (UUID attachmentId : attachmentIds) {
                if (binaryContentRepository.existsById(attachmentId)) {
                    binaryContentRepository.deleteById(attachmentId);
                }
            }
        }

        // 메시지 삭제
        messageRepository.deleteById(id);
    }

    // 메시지 내용 검증 로직
    // null, 빈 문자열, 공백만 있는 메시지를 방지
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
    }

    // 첨부파일 요청 검증 로직
    private void validateAttachment(BinaryContentCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("첨부파일 요청은 비어 있을 수 없습니다.");
        }

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