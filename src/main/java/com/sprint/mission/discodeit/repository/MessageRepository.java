package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf"
)
// Message 데이터를 저장하고 조회하기 위한 Repository 인터페이스
// Service는 이 인터페이스를 통해 Message 저장소에 접근함
public interface MessageRepository {

    // 메시지 저장
    // 새 메시지 생성 또는 기존 메시지 수정 후 저장할 때 사용
    Message save(Message message);

    // id로 메시지 한 개 조회
    Message findById(UUID id);

    // 모든 메시지 조회
    List<Message> findAll();

    // id로 메시지 삭제
    void deleteById(UUID id);

    // 해당 id를 가진 메시지가 존재하는지 확인
    boolean existsById(UUID id);

    // 특정 Channel에 작성된 메시지 목록 조회
    // MessageService의 findAllByChannelId 기능에 사용
    List<Message> findAllByChannelId(UUID channelId);

    // 추가한 부분: 특정 Channel의 가장 최근 메시지 생성 시간 조회
    // ChannelResponse의 lastMessageAt 값을 구할 때 사용
    Instant findLastMessageAtByChannelId(UUID channelId);

    // 특정 Channel에 작성된 모든 메시지 삭제
    // Channel 삭제 시 관련 Message도 같이 삭제하기 위해 사용
    void deleteByChannelId(UUID channelId);

    // 특정 User가 작성한 모든 메시지 삭제
    // User 삭제 시 관련 Message도 같이 삭제하기 위해 사용
    void deleteByAuthorId(UUID authorId);
}