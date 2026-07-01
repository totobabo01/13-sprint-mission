package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 메시지 생성 - JSON 요청 처리
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> create(
            @RequestBody MessageCreateRequest request
    ) {
        MessageResponse response = messageService.create(request);

        URI location = URI.create("/api/messages/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // 메시지 생성 - multipart/form-data 요청 처리
    // 수정됨: content, authorId, channelId를 필수값으로 받지 않도록 변경
    // 프론트가 messageCreateRequest/request/messageRequest JSON part로 보내도 처리 가능
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createWithMultipart(
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "body", required = false) String body,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "message", required = false) String messageText,

            @RequestParam(value = "authorId", required = false) UUID authorId,
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "senderId", required = false) UUID senderId,

            @RequestParam(value = "channelId", required = false) UUID channelId,
            @RequestParam(value = "roomId", required = false) UUID roomId,

            @RequestPart(value = "messageCreateRequest", required = false) String messageCreateRequestJson,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "messageRequest", required = false) String messageRequestJson,

            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {

        String finalContent = firstNonBlank(content, body, text, messageText);
        UUID finalAuthorId = firstNonNull(authorId, userId, senderId);
        UUID finalChannelId = firstNonNull(channelId, roomId);

        String json = firstNonBlank(messageCreateRequestJson, requestJson, messageRequestJson);

        if (!isBlank(json)) {
            JsonNode root = objectMapper.readTree(json);

            finalContent = firstNonBlank(
                    finalContent,
                    getText(root, "content"),
                    getText(root, "body"),
                    getText(root, "text"),
                    getText(root, "message")
            );

            finalAuthorId = firstNonNull(
                    finalAuthorId,
                    getUuid(root, "authorId"),
                    getUuid(root, "userId"),
                    getUuid(root, "senderId")
            );

            finalChannelId = firstNonNull(
                    finalChannelId,
                    getUuid(root, "channelId"),
                    getUuid(root, "roomId")
            );
        }

        List<BinaryContentCreateRequest> attachmentRequests = toBinaryContentCreateRequests(
                attachments != null ? attachments : files
        );

        MessageCreateRequest request = new MessageCreateRequest(
                finalContent,
                finalAuthorId,
                finalChannelId,
                attachmentRequests
        );

        MessageResponse response = messageService.create(request);

        URI location = URI.create("/api/messages/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // 메시지 단건 조회
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> read(@PathVariable UUID messageId) {
        MessageResponse response = messageService.read(messageId);

        return ResponseEntity.ok(response);
    }

    // 특정 채널의 메시지 목록 조회
    @GetMapping
    public ResponseEntity<List<MessageResponse>> findAllByChannelId(
            @RequestParam UUID channelId
    ) {
        List<MessageResponse> responses = messageService.findAllByChannelId(channelId);

        return ResponseEntity.ok(responses);
    }

    // 메시지 수정
    @PatchMapping
    public ResponseEntity<MessageResponse> update(
            @RequestBody MessageUpdateRequest request
    ) {
        MessageResponse response = messageService.update(request);

        return ResponseEntity.ok(response);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private List<BinaryContentCreateRequest> toBinaryContentCreateRequests(
            List<MultipartFile> files
    ) throws IOException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<BinaryContentCreateRequest> requests = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );

            requests.add(request);
        }

        return requests;
    }

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

    @SafeVarargs
    private final <T> T firstNonNull(T... values) {
        if (values == null) {
            return null;
        }

        for (T value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private String getText(JsonNode root, String fieldName) {
        if (root == null || fieldName == null || !root.has(fieldName)) {
            return null;
        }

        JsonNode node = root.get(fieldName);

        if (node == null || node.isNull()) {
            return null;
        }

        return node.asText();
    }

    private UUID getUuid(JsonNode root, String fieldName) {
        String value = getText(root, fieldName);

        if (isBlank(value)) {
            return null;
        }

        return UUID.fromString(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
