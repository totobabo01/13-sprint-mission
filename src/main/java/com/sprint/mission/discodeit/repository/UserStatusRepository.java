package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

// UserStatus 데이터를 저장하고 조회하기 위한 Repository 인터페이스
// UserStatus는 사용자의 온라인 상태, 마지막 접속 시간을 관리함
public interface UserStatusRepository {

    // UserStatus 저장
    UserStatus save(UserStatus userStatus);

    // UserStatus id로 단건 조회
    UserStatus findById(UUID id);

    // 전체 UserStatus 조회
    List<UserStatus> findAll();

    // UserStatus id로 삭제
    void deleteById(UUID id);

    // UserStatus id 존재 여부 확인
    boolean existsById(UUID id);

    // userId로 UserStatus 조회
    // 특정 사용자의 상태 정보를 찾을 때 사용
    UserStatus findByUserId(UUID userId);

    // 추가한 부분:
    // userId에 해당하는 UserStatus가 존재하는지 확인
    boolean existsByUserId(UUID userId);

    // userId로 UserStatus 삭제
    // 사용자가 삭제될 때 해당 사용자의 상태 정보도 같이 삭제할 때 사용
    void deleteByUserId(UUID userId);
}