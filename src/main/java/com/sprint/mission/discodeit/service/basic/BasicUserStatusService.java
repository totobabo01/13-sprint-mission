package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

// UserStatus 기능을 실제로 구현하는 Service 클래스
// UserStatusService 인터페이스의 기능들을 구현함
@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    // UserStatus 데이터를 저장하고 조회하기 위한 Repository
    private final UserStatusRepository userStatusRepository;

    // User가 실제로 존재하는지 확인하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 UserService 대신 UserRepository 사용
    private final UserRepository userRepository;

    // userId로 사용자의 상태 정보 조회
    // UserStatus의 id가 아니라 User의 id를 기준으로 조회함
    @Override
    public UserStatusResponse findByUserId(UUID userId) {
        validateUserExists(userId);

        UserStatus userStatus = userStatusRepository.findByUserId(userId);

        if (userStatus == null) {
            throw new IllegalArgumentException("조회할 사용자 상태 정보를 찾을 수 없습니다.");
        }

        return toResponse(userStatus);
    }

    // 사용자를 온라인 상태로 변경
    // 로그인 성공 시 호출할 수 있음
    @Override
    public UserStatusResponse updateOnline(UUID userId) {
        validateUserExists(userId);

        UserStatus userStatus = userStatusRepository.findByUserId(userId);

        // UserStatus가 아직 없으면 새로 생성
        if (userStatus == null) {
            userStatus = new UserStatus(userId);
        }

        // UserStatus 엔티티 내부 메서드로 온라인 상태 변경
        userStatus.updateOnline();

        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    // 사용자를 오프라인 상태로 변경
    // 로그아웃 또는 접속 종료 시 호출할 수 있음
    @Override
    public UserStatusResponse updateOffline(UUID userId) {
        validateUserExists(userId);

        UserStatus userStatus = userStatusRepository.findByUserId(userId);

        if (userStatus == null) {
            throw new IllegalArgumentException("오프라인으로 변경할 사용자 상태 정보를 찾을 수 없습니다.");
        }

        // UserStatus 엔티티 내부 메서드로 오프라인 상태 변경
        userStatus.updateOffline();

        userStatusRepository.save(userStatus);

        return toResponse(userStatus);
    }

    // userId로 사용자의 상태 정보 삭제
    // 사용자가 삭제될 때 UserStatus도 함께 삭제할 때 사용
    @Override
    public void deleteByUserId(UUID userId) {
        validateUserExists(userId);

        if (!userStatusRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("삭제할 사용자 상태 정보를 찾을 수 없습니다.");
        }

        userStatusRepository.deleteByUserId(userId);
    }

    // User가 실제로 존재하는지 확인하는 보조 메서드
    private void validateUserExists(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 id는 null일 수 없습니다.");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
    }

    // UserStatus 엔티티를 UserStatusResponse DTO로 변환하는 보조 메서드
    private UserStatusResponse toResponse(UserStatus userStatus) {
        return new UserStatusResponse(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId(),
                userStatus.isOnline(),
                userStatus.getLastActiveAt()
        );
    }
}