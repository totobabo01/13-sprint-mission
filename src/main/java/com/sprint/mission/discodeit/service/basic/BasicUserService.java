package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
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
        // 사용자 생성 요청의 필수값을 먼저 검증한다.
        validateCreateRequest(request);

        // username은 중복될 수 없으므로 저장 전에 확인한다.
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // email도 중복될 수 없으므로 저장 전에 확인한다.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 프로필 이미지가 포함된 경우 BinaryContent로 먼저 저장하고 id를 받아온다.
        UUID profileId = saveProfileImage(request.getProfileImage());

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        // 저장된 프로필 이미지가 있으면 사용자 엔티티에 연결한다.
        if (profileId != null) {
            user.updateProfileId(profileId);
        }

        userRepository.save(user);

        // 사용자가 생성되면 기본 UserStatus도 함께 생성한다.
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

        /*
         * 프론트 수정 요청에서는 username/email/password 중 일부만 들어올 수 있다.
         * null 또는 빈 문자열이면 기존 값을 유지하도록 보정한다.
         */
        String updateUsername = isBlank(request.getUsername())
                ? user.getUsername()
                : request.getUsername();

        String updateEmail = isBlank(request.getEmail())
                ? user.getEmail()
                : request.getEmail();

        String updatePassword = isBlank(request.getPassword())
                ? user.getPassword()
                : request.getPassword();

        // username이 변경되는 경우에만 중복 여부를 확인한다.
        if (!user.getUsername().equals(updateUsername)
                && userRepository.existsByUsername(updateUsername)) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // email이 변경되는 경우에만 중복 여부를 확인한다.
        if (!user.getEmail().equals(updateEmail)
                && userRepository.existsByEmail(updateEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.update(
                updateUsername,
                updateEmail,
                updatePassword
        );

        /*
         * 프로필 이미지 수정 처리 우선순위
         *
         * 1. profileImage가 들어온 경우:
         *    - 새 BinaryContent를 저장한다.
         *    - 기존 프로필 이미지가 있으면 삭제한다.
         *    - 새 BinaryContent id를 사용자 profileId로 연결한다.
         *
         * 2. profileId가 들어온 경우:
         *    - 이미 업로드된 BinaryContent를 사용자 profileId로 연결한다.
         *
         * 3. 둘 다 없으면:
         *    - 기존 프로필 정보를 유지한다.
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

        /*
         * 사용자 삭제 시 연관 데이터도 함께 정리한다.
         *
         * 삭제 순서:
         * 1. 사용자가 작성한 메시지의 첨부파일 BinaryContent 삭제
         * 2. 사용자가 작성한 메시지 삭제
         * 3. 사용자의 ReadStatus 삭제
         * 4. 사용자의 프로필 이미지 BinaryContent 삭제
         * 5. 사용자의 UserStatus 삭제
         * 6. 사용자 삭제
         */
        deleteMessageAttachmentsByAuthorId(id);

        messageRepository.deleteByAuthorId(id);

        readStatusRepository.deleteByUserId(id);

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userStatusRepository.deleteByUserId(id);

        userRepository.deleteById(id);
    }

    private void validateCreateRequest(UserCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("사용자 생성 요청은 비어 있을 수 없습니다.");
        }

        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException("사용자 이름은 비어 있을 수 없습니다.");
        }

        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
    }

    private void deleteMessageAttachmentsByAuthorId(UUID authorId) {
        List<Message> messages = messageRepository.findAll();

        // 같은 첨부파일 id가 중복으로 들어갈 수 있으므로 Set으로 중복 제거한다.
        Set<UUID> attachmentIds = new HashSet<>();

        for (Message message : messages) {
            // 삭제 대상 사용자가 작성한 메시지만 처리한다.
            if (message.getAuthorId() == null || !message.getAuthorId().equals(authorId)) {
                continue;
            }

            // 첨부파일이 없는 메시지는 건너뛴다.
            if (message.getAttachmentIds() == null || message.getAttachmentIds().isEmpty()) {
                continue;
            }

            attachmentIds.addAll(message.getAttachmentIds());
        }

        for (UUID attachmentId : attachmentIds) {
            binaryContentRepository.deleteById(attachmentId);
        }
    }

    private UUID saveProfileImage(BinaryContentCreateRequest profileImage) {
        // 프로필 이미지가 없는 요청이면 저장하지 않는다.
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
        return new UserResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                getOnlineStatus(user.getId())
        );
    }

    private boolean getOnlineStatus(UUID userId) {
        try {
            UserStatus userStatus = userStatusRepository.findByUserId(userId);

            /*
             * 복구 가능한 케이스:
             * UserStatus가 존재하지 않는 경우
             *
             * 사용자 생성 과정에서 UserStatus가 누락되었거나,
             * 기존 데이터에 UserStatus가 없는 경우일 수 있다.
             * 이 경우에는 실제 장애로 보지 않고 offline 상태로 응답한다.
             */
            if (userStatus == null) {
                log.warn("UserStatus가 존재하지 않아 offline 상태로 응답합니다. userId={}", userId);
                return false;
            }

            return userStatus.isOnline();
        } catch (RuntimeException e) {
            /*
             * 실제 장애 케이스:
             * - UserStatus 저장 파일 손상
             * - 역직렬화 실패
             * - Repository 내부 조회 오류
             *
             * 이런 오류를 online=false로 조용히 숨기면 문제를 발견하기 어렵다.
             * 따라서 IllegalStateException으로 다시 던져 장애 상황을 명확히 드러낸다.
             */
            throw new IllegalStateException(
                    "UserStatus 조회 중 오류가 발생했습니다. 데이터 파일 손상 또는 저장소 오류 가능성이 있습니다. userId=" + userId,
                    e
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}