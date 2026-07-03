package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.mapper.UserMultipartMapper;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/users", "/api/user"})
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMultipartMapper userMultipartMapper;

    // JSON 사용자 생성
    // POST /api/users
    // Content-Type: application/json
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> create(
            @RequestBody UserCreateRequest request
    ) {
        UserResponse response = userService.create(request);

        return created(response);
    }

    // multipart/form-data 사용자 생성
    // 제공 API 스펙 기준:
    // - userCreateRequest: 사용자 생성 요청 JSON
    // - profile: 프로필 이미지 파일
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> createWithMultipart(
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        UserCreateRequest request = userMultipartMapper.toCreateRequest(
                userCreateRequest,
                profile
        );

        UserResponse response = userService.create(request);

        return created(response);
    }

    // 사용자 단건 조회
    // GET /api/users/{id}
    // GET /api/user/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> read(@PathVariable UUID id) {
        UserResponse response = userService.read(id);

        return ResponseEntity.ok(response);
    }

    // 사용자 목록 조회
    // GET /api/users
    // GET /api/user
    @GetMapping
    public ResponseEntity<List<UserResponse>> readAll() {
        List<UserResponse> responses = userService.readAll();

        return ResponseEntity.ok(responses);
    }

    // 프론트 호환용 목록 조회
    // GET /api/user/findAll
    // GET /api/users/findAll
    @GetMapping("/findAll")
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> responses = userService.readAll();

        return ResponseEntity.ok(responses);
    }

    // JSON 사용자 수정
    // PATCH /api/users/{id}
    // Content-Type: application/json
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request
    ) {
        UserUpdateRequest fixedRequest = new UserUpdateRequest(
                id,
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getProfileId(),
                request.getProfileImage()
        );

        UserResponse response = userService.update(fixedRequest);

        return ResponseEntity.ok(response);
    }

    // multipart/form-data 사용자 수정
    // 제공 API 스펙 기준:
    // - userUpdateRequest: 사용자 수정 요청 JSON
    // - profile: 프로필 이미지 파일
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateWithMultipart(
            @PathVariable UUID id,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile
    ) throws IOException {
        UserUpdateRequest request = userMultipartMapper.toUpdateRequest(
                id,
                userUpdateRequest,
                profile
        );

        UserResponse response = userService.update(request);

        return ResponseEntity.ok(response);
    }

    // 사용자 삭제
    // DELETE /api/users/{id}
    // DELETE /api/user/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    private ResponseEntity<UserResponse> created(UserResponse response) {
        URI location = URI.create("/api/users/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }
}