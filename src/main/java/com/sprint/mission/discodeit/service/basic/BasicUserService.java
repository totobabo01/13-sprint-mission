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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        // 수정됨: 필수값 검증 추가
        if (isBlank(request.getUsername())) {
            throw new IllegalArgumentException("사용자 이름은 비어 있을 수 없습니다.");
        }

        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
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

        /*
         * 수정됨:
         * 프론트 수정 요청에서는 username/email/password 중 일부만 들어올 수 있음.
         * null 또는 빈 문자열이면 기존 값을 유지하도록 보정.
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

        // 수정됨: 보정된 username으로 중복 검사
        if (!user.getUsername().equals(updateUsername)
                && userRepository.existsByUsername(updateUsername)) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // 수정됨: 보정된 email로 중복 검사
        if (!user.getEmail().equals(updateEmail)
                && userRepository.existsByEmail(updateEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 수정됨: null이 아닌 최종 값으로 update 호출
        user.update(
                updateUsername,
                updateEmail,
                updatePassword
        );

        /*
         * 1순위: profileImage가 들어오면 새 BinaryContent를 생성해서 연결
         * 2순위: profileId가 들어오면 이미 업로드된 BinaryContent id를 바로 연결
         * 둘 다 없으면 기존 프로필 유지
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

        // 사용자가 작성한 메시지의 첨부파일 BinaryContent 먼저 삭제
        deleteMessageAttachmentsByAuthorId(id);

        // 사용자가 작성한 메시지 삭제
        messageRepository.deleteByAuthorId(id);

        // 사용자의 읽음 상태 삭제
        readStatusRepository.deleteByUserId(id);

        // 사용자 프로필 이미지 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        // 사용자 온라인 상태 삭제
        userStatusRepository.deleteByUserId(id);

        // 사용자 삭제
        userRepository.deleteById(id);
    }

    private void deleteMessageAttachmentsByAuthorId(UUID authorId) {
        List<Message> messages = messageRepository.findAll();

        Set<UUID> attachmentIds = new HashSet<>();

        for (Message message : messages) {
            if (message.getAuthorId() == null || !message.getAuthorId().equals(authorId)) {
                continue;
            }

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
        boolean online = false;

        /*
         * 수정됨:
         * UserStatus 저장 파일이 깨졌거나 없는 경우에도 사용자 목록 조회가 500으로 터지지 않도록 방어.
         * 상태 조회 실패 시 online=false로 응답.
         */
        try {
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId());

            if (userStatus != null) {
                online = userStatus.isOnline();
            }
        } catch (RuntimeException e) {
            online = false;
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

    // 수정됨: null 또는 공백 문자열 체크용 공통 메서드
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}