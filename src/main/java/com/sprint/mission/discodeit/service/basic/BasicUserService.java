package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    // 수정한 부분: 프로필 이미지 BinaryContent 저장/삭제를 위해 Repository 주입
    private final BinaryContentRepository binaryContentRepository;

    // 수정한 부분: 사용자 온라인 상태 생성을 위해 UserStatusRepository 주입
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        // 수정한 부분: DTO 자체가 null인지 검증
        if (request == null) {
            throw new IllegalArgumentException("사용자 생성 요청은 비어 있을 수 없습니다.");
        }

        // 수정한 부분: username 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // 수정한 부분: email 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 수정한 부분: 프로필 이미지가 있으면 BinaryContent로 저장하고 id를 받아옴
        UUID profileId = saveProfileImage(request.getProfileImage());

        // 수정한 부분: DTO에서 값을 꺼내 User 생성
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        // 수정한 부분: 프로필 이미지가 있으면 User에 profileId 연결
        if (profileId != null) {
            user.updateProfileId(profileId);
        }

        userRepository.save(user);

        // 수정한 부분: User 생성 시 UserStatus도 함께 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        // 수정한 부분: User 엔티티를 그대로 반환하지 않고 UserResponse로 변환해서 반환
        return toResponse(user);
    }

    @Override
    public UserResponse read(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("조회할 사용자를 찾을 수 없습니다.");
        }

        // 수정한 부분: User 엔티티 대신 비밀번호가 제외된 UserResponse 반환
        return toResponse(user);
    }

    @Override
    public List<UserResponse> readAll() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();

        // 수정한 부분: 모든 User를 UserResponse로 변환
        for (User user : users) {
            responses.add(toResponse(user));
        }

        return responses;
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        // 수정한 부분: DTO 자체가 null인지 검증
        if (request == null) {
            throw new IllegalArgumentException("사용자 수정 요청은 비어 있을 수 없습니다.");
        }

        User user = userRepository.findById(request.getId());

        if (user == null) {
            throw new IllegalArgumentException("수정할 사용자를 찾을 수 없습니다.");
        }

        // 수정한 부분: username이 변경되는 경우에만 중복 검사
        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // 수정한 부분: email이 변경되는 경우에만 중복 검사
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 수정한 부분: 기존 사용자 정보 수정
        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        // 수정한 부분: 새 프로필 이미지가 있으면 기존 프로필 이미지를 삭제하고 새 이미지로 교체
        if (request.getProfileImage() != null) {
            UUID oldProfileId = user.getProfileId();

            if (oldProfileId != null) {
                binaryContentRepository.deleteById(oldProfileId);
            }

            UUID newProfileId = saveProfileImage(request.getProfileImage());
            user.updateProfileId(newProfileId);
        }

        userRepository.save(user);

        return toResponse(user);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("삭제할 사용자를 찾을 수 없습니다.");
        }

        // 수정한 부분: 사용자의 프로필 이미지가 있으면 BinaryContent도 같이 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        // 수정한 부분: UserStatus도 같이 삭제
        userStatusRepository.deleteByUserId(id);

        // 수정한 부분: 마지막에 User 삭제
        userRepository.deleteById(id);
    }

    // 수정한 부분: 프로필 이미지 DTO를 BinaryContent로 변환 후 저장하는 보조 메서드
    // profileImage가 null이면 프로필 이미지 없이 생성하는 것이므로 null 반환
    private UUID saveProfileImage(BinaryContentCreateRequest profileImage) {
        if (profileImage == null) {
            return null;
        }

        BinaryContent binaryContent = new BinaryContent(
                profileImage.getFileName(),
                profileImage.getContentType(),
                profileImage.getBytes()
        );

        binaryContentRepository.save(binaryContent);

        return binaryContent.getId();
    }

    // 수정한 부분: User 엔티티를 UserResponse DTO로 변환하는 보조 메서드
    // password는 응답에 포함하지 않음
    private UserResponse toResponse(User user) {
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId());

        boolean online = false;

        if (userStatus != null) {
            online = userStatus.isOnline();
        }

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