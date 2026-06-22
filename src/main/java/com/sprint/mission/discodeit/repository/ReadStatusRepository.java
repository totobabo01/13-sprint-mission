package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf"
)
// ReadStatus 데이터를 저장하고 조회하기 위한 Repository 인터페이스
// ReadStatus는 특정 User가 특정 Channel을 어디까지 읽었는지 나타내는 도메인
public interface ReadStatusRepository {

    // ReadStatus 저장
    // 새 ReadStatus 생성 또는 기존 ReadStatus 수정 후 저장할 때 사용
    ReadStatus save(ReadStatus readStatus);

    // id로 ReadStatus 한 개 조회
    ReadStatus findById(UUID id);

    // 모든 ReadStatus 조회
    List<ReadStatus> findAll();

    // id로 ReadStatus 삭제
    void deleteById(UUID id);

    // 해당 id를 가진 ReadStatus가 존재하는지 확인
    boolean existsById(UUID id);

    // 특정 User와 특정 Channel에 해당하는 ReadStatus 조회
    // 같은 User와 Channel 조합의 ReadStatus 중복 생성 방지에 사용
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 특정 User의 모든 ReadStatus 조회
    // 사용자가 참여한 PRIVATE 채널을 찾을 때 사용할 수 있음
    List<ReadStatus> findAllByUserId(UUID userId);

    // 특정 Channel의 모든 ReadStatus 조회
    // PRIVATE 채널 참여자 목록을 만들 때 사용할 수 있음
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // 추가한 부분: 특정 User가 참여한 Channel id 목록 조회
    // ChannelService에서 PRIVATE 채널 조회 권한을 판단할 때 사용
    List<UUID> findChannelIdsByUserId(UUID userId);

    // 추가한 부분: 특정 Channel에 참여한 User id 목록 조회
    // ChannelResponse의 participantUserIds를 만들 때 사용
    List<UUID> findUserIdsByChannelId(UUID channelId);

    // 특정 Channel과 관련된 모든 ReadStatus 삭제
    // 채널 삭제 시 관련 ReadStatus도 같이 삭제하기 위해 사용
    void deleteByChannelId(UUID channelId);

    // 추가한 부분: 특정 User와 관련된 모든 ReadStatus 삭제
    // User 삭제 시 관련 ReadStatus도 같이 삭제하기 위해 사용
    void deleteByUserId(UUID userId);
}