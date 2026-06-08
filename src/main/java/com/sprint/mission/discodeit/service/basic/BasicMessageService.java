package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        // 수정한 부분: 메시지 생성 전에 내용이 비어 있거나 공백인지 검증
        validateContent(content);

        if (!userRepository.existsById(authorId)) {
            throw new IllegalArgumentException("메시지를 작성할 사용자를 찾을 수 없습니다.");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("메시지를 작성할 채널을 찾을 수 없습니다.");
        }

        Message message = new Message(content, authorId, channelId);
        messageRepository.save(message);

        return message;
    }

    @Override
    public Message read(UUID id) {
        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("조회할 메시지를 찾을 수 없습니다.");
        }

        return message;
    }

    @Override
    public List<Message> readAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        // 수정한 부분: 메시지 수정 전에도 내용이 비어 있거나 공백인지 검증
        validateContent(content);

        Message message = messageRepository.findById(id);

        if (message == null) {
            throw new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다.");
        }

        message.update(content);
        messageRepository.save(message);

        return message;
    }

    @Override
    public void delete(UUID id) {
        if (!messageRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }

        messageRepository.deleteById(id);
    }

    // 수정한 부분: 메시지 내용 검증 로직을 별도 메서드로 분리
    // null, 빈 문자열, 공백만 있는 메시지를 방지
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }
    }
}