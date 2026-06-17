package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

// ReadStatus 관련 기능을 정의하는 Service 인터페이스
// ReadStatus는 특정 User가 특정 Channel을 어디까지 읽었는지 관리하는 도메인
public interface ReadStatusService {

    // ReadStatus 생성 기능
    // User와 Channel 정보를 DTO로 받아 읽음 상태를 생성
    ReadStatusResponse create(ReadStatusCreateRequest request);

    // id로 ReadStatus 단건 조회
    ReadStatusResponse find(UUID id);

    // 특정 User의 모든 ReadStatus 조회
    // 사용자가 어떤 채널들을 읽었는지 확인할 때 사용
    List<ReadStatusResponse> findAllByUserId(UUID userId);

    // ReadStatus 수정 기능
    // 마지막으로 읽은 시간 lastReadAt을 수정할 때 사용
    ReadStatusResponse update(ReadStatusUpdateRequest request);

    // id로 ReadStatus 삭제
    void delete(UUID id);
}