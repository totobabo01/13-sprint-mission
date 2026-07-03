package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class UserMultipartMapper {

    public UserCreateRequest toCreateRequest(
            UserCreateRequest request,
            MultipartFile profile
    ) throws IOException {
        BinaryContentCreateRequest profileImage =
                toBinaryContentCreateRequest(profile);

        return new UserCreateRequest(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                profileImage
        );
    }

    public UserUpdateRequest toUpdateRequest(
            UUID id,
            UserUpdateRequest request,
            MultipartFile profile
    ) throws IOException {
        BinaryContentCreateRequest profileImage =
                toBinaryContentCreateRequest(profile);

        return new UserUpdateRequest(
                id,
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getProfileId(),
                profileImage
        );
    }

    private BinaryContentCreateRequest toBinaryContentCreateRequest(
            MultipartFile file
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        return new BinaryContentCreateRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );
    }
}