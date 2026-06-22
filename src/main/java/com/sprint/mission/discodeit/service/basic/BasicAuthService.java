package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 인증 기능을 실제로 구현하는 Service 클래스
// AuthService 인터페이스의 login 기능을 구현함
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    // 사용자 정보를 조회하기 위한 Repository
    // 같은 Service 계층끼리 의존하지 않기 위해 UserService 대신 UserRepository 사용
    private final UserRepository userRepository;

    // 사용자의 온라인 상태를 조회/수정하기 위한 Repository
    // 로그인 성공 시 UserStatus의 online, lastActiveAt 값을 갱신하기 위해 사용
    private final UserStatusRepository userStatusRepository;

    // 로그인 기능
    // username, password가 일치하는 사용자가 있으면 UserStatus를 온라인 상태로 갱신 후 UserResponse 반환
    // 일치하는 사용자가 없으면 예외 발생
    @Override
    public UserResponse login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("로그인 요청은 비어 있을 수 없습니다.");
        }

        // username으로 사용자 조회
        User user = userRepository.findByUsername(request.getUsername());

        // username에 해당하는 사용자가 없으면 예외 발생
        if (user == null) {
            throw new IllegalArgumentException("사용자 이름 또는 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호가 일치하지 않으면 예외 발생
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("사용자 이름 또는 비밀번호가 올바르지 않습니다.");
        }

        // 추가한 부분:
        // 로그인 성공 시 UserStatus를 온라인 상태로 변경하고 마지막 활동 시간을 갱신
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId());

        if (userStatus == null) {
            userStatus = new UserStatus(user.getId());
        }

        userStatus.updateOnline();
        userStatusRepository.save(userStatus);

        // 로그인 성공 시 User 엔티티를 UserResponse DTO로 변환해서 반환
        return toResponse(user);
    }

    // User 엔티티를 UserResponse DTO로 변환하는 보조 메서드
    private UserResponse toResponse(User user) {
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId());

        boolean online = userStatus != null && userStatus.isOnline();

        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }
}