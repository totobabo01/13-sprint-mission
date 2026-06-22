package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("사용자 생성 요청은 비어 있을 수 없습니다.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UUID profileId = saveProfileImage(request.getProfileImage());

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        if (profileId != null) {
            user.updateProfileId(profileId);
        }

        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return toResponse(user);
    }

    @Override
    public UserResponse read(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException("조회할 사용자를 찾을 수 없습니다.");
        }

        return toResponse(user);
    }

    @Override
    public List<UserResponse> readAll() {
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            responses.add(toResponse(user));
        }

        return responses;
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("사용자 수정 요청은 비어 있을 수 없습니다.");
        }

        if (request.getId() == null) {
            throw new IllegalArgumentException("수정할 사용자 id는 null일 수 없습니다.");
        }

        User user = userRepository.findById(request.getId());

        if (user == null) {
            throw new IllegalArgumentException("수정할 사용자를 찾을 수 없습니다.");
        }

        if (request.getUsername() != null
                && !user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        if (request.getEmail() != null
                && !user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        /*
         * 1순위: profileImage가 들어오면 새 BinaryContent를 생성해서 연결
         * 2순위: profileId가 들어오면 이미 업로드된 BinaryContent id를 바로 연결
         */
        if (request.getProfileImage() != null) {
            UUID oldProfileId = user.getProfileId();

            if (oldProfileId != null) {
                binaryContentRepository.deleteById(oldProfileId);
            }

            UUID newProfileId = saveProfileImage(request.getProfileImage());
            user.updateProfileId(newProfileId);
        } else if (request.getProfileId() != null) {
            user.updateProfileId(request.getProfileId());
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

        messageRepository.deleteByAuthorId(id);
        readStatusRepository.deleteByUserId(id);

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userStatusRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

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