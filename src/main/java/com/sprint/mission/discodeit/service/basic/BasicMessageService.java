package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ChannelRepository channelRepository
    ) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
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
}