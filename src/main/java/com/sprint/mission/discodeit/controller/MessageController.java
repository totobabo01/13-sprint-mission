package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.mapper.MessageMultipartMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageMultipartMapper messageMultipartMapper;

    // 메시지 생성 - JSON 요청 처리
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> create(
            @RequestBody MessageCreateRequest request
    ) {
        MessageResponse response = messageService.create(request);

        return created(response);
    }

    // 메시지 생성 - multipart/form-data 요청 처리
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> createWithMultipart(
            @RequestParam MultiValueMap<String, String> formData,

            @RequestPart(value = "messageCreateRequest", required = false) String messageCreateRequestJson,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "messageRequest", required = false) String messageRequestJson,

            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        MessageCreateRequest request = messageMultipartMapper.toCreateRequest(
                formData,
                messageCreateRequestJson,
                requestJson,
                messageRequestJson,
                attachments,
                files
        );

        MessageResponse response = messageService.create(request);

        return created(response);
    }

    // 메시지 단건 조회
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> read(@PathVariable UUID messageId) {
        MessageResponse response = messageService.read(messageId);

        return ResponseEntity.ok(response);
    }

    /*
     * 특정 채널의 메시지 목록 조회
     *
     * 프론트 요청 예시:
     * GET /api/messages?channelId=...&page=0&size=50&sort=createdAt,desc
     *
     * 프론트가 Page 응답 형태를 기대할 수 있으므로
     * content, totalElements, totalPages, page, size 등을 같이 내려준다.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAllByChannelId(
            @RequestParam UUID channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        List<MessageResponse> allResponses = messageService.findAllByChannelId(channelId);

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 50 : size;

        int totalElements = allResponses.size();
        int totalPages = totalElements == 0
                ? 0
                : (int) Math.ceil((double) totalElements / safeSize);

        int fromIndex = Math.min(safePage * safeSize, totalElements);
        int toIndex = Math.min(fromIndex + safeSize, totalElements);

        List<MessageResponse> pageContent = allResponses.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageContent);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("page", safePage);
        response.put("size", safeSize);
        response.put("number", safePage);
        response.put("first", safePage == 0);
        response.put("last", totalPages == 0 || safePage >= totalPages - 1);
        response.put("hasNext", totalPages > 0 && safePage < totalPages - 1);
        response.put("sort", sort);

        return ResponseEntity.ok(response);
    }

    // 메시지 수정
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable UUID messageId,
            @RequestBody MessageUpdateRequest request
    ) {
        MessageUpdateRequest fixedRequest = new MessageUpdateRequest(
                messageId,
                request.getContent()
        );

        MessageResponse response = messageService.update(fixedRequest);

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

    private ResponseEntity<MessageResponse> created(MessageResponse response) {
        URI location = URI.create("/api/messages/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }
}