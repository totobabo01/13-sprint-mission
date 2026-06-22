package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf"
)
// Message 데이터를 메모리에 저장하고 조회하는 Repository 구현체
// JCF의 HashMap을 사용해서 데이터를 저장함
public class JCFMessageRepository implements MessageRepository {

    // Message 데이터를 저장하는 Map
    // key: Message의 id
    // value: Message 객체
    private final Map<UUID, Message> data;

    // 생성자: Message 데이터를 저장할 HashMap 초기화
    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    // 메시지 저장
    // 새 메시지면 추가되고, 같은 id의 메시지가 있으면 덮어쓰기됨
    @Override
    public Message save(Message message) {
        UUID id = message.getId();
        data.put(id, message);

        return message;
    }

    // id로 메시지 한 개 조회
    // 해당 id의 메시지가 없으면 null 반환
    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    // 저장된 모든 메시지 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    // id로 메시지 삭제
    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    // 해당 id의 메시지가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    // 특정 Channel에 작성된 메시지 목록 조회
    // MessageService의 findAllByChannelId 기능에 사용
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> result = new ArrayList<>();

        for (Message message : data.values()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }

        return result;
    }

    // 추가한 부분: 특정 Channel의 가장 최근 메시지 생성 시간 조회
    // ChannelResponse의 lastMessageAt 값을 구할 때 사용
    @Override
    public Instant findLastMessageAtByChannelId(UUID channelId) {
        Instant lastMessageAt = null;

        for (Message message : data.values()) {
            if (message.getChannelId().equals(channelId)) {
                if (lastMessageAt == null || message.getCreatedAt().isAfter(lastMessageAt)) {
                    lastMessageAt = message.getCreatedAt();
                }
            }
        }

        return lastMessageAt;
    }

    // 특정 Channel에 작성된 모든 메시지 삭제
    // Channel 삭제 시 관련 Message도 같이 삭제하기 위해 사용
    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(message -> message.getChannelId().equals(channelId));
    }

    // 특정 User가 작성한 모든 메시지 삭제
    // User 삭제 시 관련 Message도 같이 삭제하기 위해 사용
    @Override
    public void deleteByAuthorId(UUID authorId) {
        data.values().removeIf(message -> message.getAuthorId().equals(authorId));
    }
}