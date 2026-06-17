package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// ReadStatus 기능을 실제로 구현하는 Service 클래스
// ReadStatusService 인터페이스의 기능들을 구현함
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    // ReadStatus 데이터를 저장하고 조회하기 위한 Repository
    private final ReadStatusRepository readStatusRepository;

    // User 존재 여부를 확인하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 UserService 대신 UserRepository 사용
    private final UserRepository userRepository;

    // Channel 존재 여부를 확인하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 ChannelService 대신 ChannelRepository 사용
    private final ChannelRepository channelRepository;

    // ReadStatus 생성
    // 특정 User가 특정 Channel에 대한 읽음 상태를 생성함
    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("읽음 상태 생성 요청은 비어 있을 수 없습니다.");
        }

        UUID userId = request.getUserId();
        UUID channelId = request.getChannelId();

        // 관련 User가 존재하지 않으면 예외 발생
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("읽음 상태를 생성할 사용자를 찾을 수 없습니다.");
        }

        // 관련 Channel이 존재하지 않으면 예외 발생
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("읽음 상태를 생성할 채널을 찾을 수 없습니다.");
        }

        // 같은 User와 Channel 조합의 ReadStatus가 이미 있으면 예외 발생
        ReadStatus existingReadStatus =
                readStatusRepository.findByUserIdAndChannelId(userId, channelId);

        if (existingReadStatus != null) {
            throw new IllegalArgumentException("이미 해당 사용자와 채널의 읽음 상태가 존재합니다.");
        }

        // ReadStatus 엔티티 생성
        ReadStatus readStatus = new ReadStatus(userId, channelId);

        // Repository에 저장
        readStatusRepository.save(readStatus);

        // 응답 DTO로 변환해서 반환
        return toResponse(readStatus);
    }

    // id로 ReadStatus 단건 조회
    @Override
    public ReadStatusResponse find(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id);

        if (readStatus == null) {
            throw new IllegalArgumentException("조회할 읽음 상태를 찾을 수 없습니다.");
        }

        return toResponse(readStatus);
    }

    // 특정 User의 모든 ReadStatus 조회
    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        // User가 존재하지 않으면 예외 발생
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("읽음 상태를 조회할 사용자를 찾을 수 없습니다.");
        }

        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);
        List<ReadStatusResponse> responses = new ArrayList<>();

        for (ReadStatus readStatus : readStatuses) {
            responses.add(toResponse(readStatus));
        }

        return responses;
    }

    // ReadStatus 수정
    // 마지막으로 읽은 시간을 현재 시간으로 갱신함
    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("읽음 상태 수정 요청은 비어 있을 수 없습니다.");
        }

        ReadStatus readStatus = readStatusRepository.findById(request.getId());

        if (readStatus == null) {
            throw new IllegalArgumentException("수정할 읽음 상태를 찾을 수 없습니다.");
        }

        // 수정한 부분:
        // ReadStatus 엔티티의 updateLastReadAt()이 인자를 받지 않는 구조라면 이렇게 호출해야 함
        readStatus.updateLastReadAt();

        // 수정된 ReadStatus 저장
        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    // id로 ReadStatus 삭제
    @Override
    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 읽음 상태를 찾을 수 없습니다.");
        }

        readStatusRepository.deleteById(id);
    }

    // ReadStatus 엔티티를 ReadStatusResponse DTO로 변환하는 보조 메서드
    private ReadStatusResponse toResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}