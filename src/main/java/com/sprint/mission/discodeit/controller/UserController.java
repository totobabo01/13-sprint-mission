package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
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

    // 수정됨: multipart 내부 JSON 파트를 읽기 위해 사용
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 기존 방식: JSON 사용자 생성
    // POST /api/users
    // Content-Type: application/json
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request) {
        UserResponse response = userService.create(request);

        URI location = URI.create("/api/users/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // 수정됨: 프론트엔드 FormData 사용자 생성 처리
    // POST /api/users
    // Content-Type: multipart/form-data
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> createWithMultipart(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,

            // 수정됨: 프론트가 name으로 보낼 가능성 대비
            @RequestParam(value = "name", required = false) String name,

            // 수정됨: 프론트가 JSON 파트로 사용자 정보를 보낼 가능성 대비
            @RequestPart(value = "userCreateRequest", required = false) String userCreateRequestJson,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "user", required = false) String userJson,

            // 수정됨: 프로필 이미지 파일 파트 이름 여러 경우 대비
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profile", required = false) MultipartFile profile,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        username = firstNonBlank(username, name);

        String requestBodyJson = firstNonBlank(userCreateRequestJson, requestJson, userJson);

        if (requestBodyJson != null) {
            JsonNode root = objectMapper.readTree(requestBodyJson);

            username = firstNonBlank(username, getText(root, "username"), getText(root, "name"));
            email = firstNonBlank(email, getText(root, "email"));
            password = firstNonBlank(password, getText(root, "password"));
        }

        if (isBlank(username) || isBlank(email) || isBlank(password)) {
            throw new IllegalArgumentException("username, email, password는 필수입니다.");
        }

        MultipartFile selectedProfileImage = firstFile(profileImage, profile, image);

        BinaryContentCreateRequest binaryContentCreateRequest =
                toBinaryContentCreateRequest(selectedProfileImage);

        UserCreateRequest request = new UserCreateRequest(
                username,
                email,
                password,
                binaryContentCreateRequest
        );

        UserResponse response = userService.create(request);

        URI location = URI.create("/api/users/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
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

    // 심화 요구사항: 기존 프론트 호환용
    // GET /api/user/findAll
    // GET /api/users/findAll
    @GetMapping("/findAll")
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> responses = userService.readAll();

        return ResponseEntity.ok(responses);
    }

    // 기존 방식: JSON 사용자 수정
    // PATCH /api/users/{id}
    // Content-Type: application/json
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request
    ) {
        // 수정됨: 프론트가 Body에 id를 안 보내도 URL의 id를 사용하도록 보정
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

    // 수정됨: 프론트엔드 FormData 사용자 수정 처리
    // PATCH /api/users/{id}
    // Content-Type: multipart/form-data
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateWithMultipart(
            @PathVariable UUID id,

            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "profileId", required = false) UUID profileId,

            // 수정됨: 수정 요청이 JSON 파트로 들어올 수 있어서 추가
            @RequestPart(value = "userUpdateRequest", required = false) String userUpdateRequestJson,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "user", required = false) String userJson,

            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profile", required = false) MultipartFile profile,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        username = firstNonBlank(username, name);

        String requestBodyJson = firstNonBlank(userUpdateRequestJson, requestJson, userJson);

        if (requestBodyJson != null) {
            JsonNode root = objectMapper.readTree(requestBodyJson);

            username = firstNonBlank(username, getText(root, "username"), getText(root, "name"));
            email = firstNonBlank(email, getText(root, "email"));
            password = firstNonBlank(password, getText(root, "password"));

            // 수정됨: JSON 파트 안에 profileId가 들어오는 경우 처리
            UUID profileIdFromJson = getUuid(root, "profileId");
            if (profileId == null) {
                profileId = profileIdFromJson;
            }
        }

        MultipartFile selectedProfileImage = firstFile(profileImage, profile, image);

        BinaryContentCreateRequest binaryContentCreateRequest =
                toBinaryContentCreateRequest(selectedProfileImage);

        // 수정됨: URL의 id를 사용해서 수정 대상 보장
        // 새 이미지 파일이 있으면 profileImage 사용
        // 새 이미지 파일이 없고 기존 profileId가 있으면 profileId 유지
        UserUpdateRequest request = new UserUpdateRequest(
                id,
                username,
                email,
                password,
                profileId,
                binaryContentCreateRequest
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

    // 수정됨: MultipartFile을 BinaryContentCreateRequest로 변환하는 공통 메서드
    private BinaryContentCreateRequest toBinaryContentCreateRequest(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        return new BinaryContentCreateRequest(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );
    }

    // 수정됨: 여러 문자열 중 비어 있지 않은 첫 번째 값 선택
    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }

        return null;
    }

    // 수정됨: 여러 파일 중 실제 업로드된 첫 번째 파일 선택
    private MultipartFile firstFile(MultipartFile... files) {
        if (files == null) {
            return null;
        }

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                return file;
            }
        }

        return null;
    }

    // 수정됨: JSON에서 문자열 필드 안전하게 추출
    private String getText(JsonNode root, String fieldName) {
        if (root == null || root.get(fieldName) == null || root.get(fieldName).isNull()) {
            return null;
        }

        return root.get(fieldName).asText();
    }

    // 수정됨: JSON에서 UUID 필드 안전하게 추출
    private UUID getUuid(JsonNode root, String fieldName) {
        String value = getText(root, fieldName);

        if (isBlank(value)) {
            return null;
        }

        return UUID.fromString(value);
    }

    // 수정됨: 문자열 공백 검사용
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}