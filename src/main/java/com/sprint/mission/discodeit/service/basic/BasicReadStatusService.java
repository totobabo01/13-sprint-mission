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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("읽음 상태 생성 요청은 비어 있을 수 없습니다.");
        }

        UUID userId = request.getUserId();
        UUID channelId = request.getChannelId();

        if (userId == null) {
            throw new IllegalArgumentException("사용자 id는 필수입니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 id는 필수입니다.");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("읽음 상태를 생성할 사용자를 찾을 수 없습니다.");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("읽음 상태를 생성할 채널을 찾을 수 없습니다.");
        }

        ReadStatus existingReadStatus =
                readStatusRepository.findByUserIdAndChannelId(userId, channelId);

        // 수정됨: 이미 있으면 예외로 터뜨리지 말고 기존 ReadStatus 반환
        // 프론트가 중복 생성 요청을 보내도 500/400으로 죽지 않게 하기 위함
        if (existingReadStatus != null) {
            return toResponse(existingReadStatus);
        }

        ReadStatus readStatus = new ReadStatus(userId, channelId);

        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("읽음 상태 id는 필수입니다.");
        }

        ReadStatus readStatus = readStatusRepository.findById(id);

        if (readStatus == null) {
            throw new IllegalArgumentException("조회할 읽음 상태를 찾을 수 없습니다.");
        }

        return toResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 id는 필수입니다.");
        }

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

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("읽음 상태 수정 요청은 비어 있을 수 없습니다.");
        }

        UUID readStatusId = request.getId();

        // 수정됨: id가 null이면 바로 명확하게 예외 처리
        // 이 경우 Controller에서 PathVariable id를 DTO에 넣어줘야 함
        if (readStatusId == null) {
            throw new IllegalArgumentException("수정할 읽음 상태 id는 필수입니다.");
        }

        ReadStatus readStatus = readStatusRepository.findById(readStatusId);

        if (readStatus == null) {
            throw new IllegalArgumentException("수정할 읽음 상태를 찾을 수 없습니다.");
        }

        // 수정됨:
        // 프론트는 newLastActiveAt을 보내지만,
        // 현재 ReadStatus 엔티티가 updateLastReadAt() 무인자 메서드 구조라면
        // 요청 시간을 직접 넣지 않고 현재 시간 기준으로 갱신
        readStatus.updateLastReadAt();

        readStatusRepository.save(readStatus);

        return toResponse(readStatus);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("삭제할 읽음 상태 id는 필수입니다.");
        }

        if (!readStatusRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 읽음 상태를 찾을 수 없습니다.");
        }

        readStatusRepository.deleteById(id);
    }

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