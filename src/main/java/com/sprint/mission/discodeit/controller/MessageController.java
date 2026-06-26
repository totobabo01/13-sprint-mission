package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 생성
    @PostMapping
    public ResponseEntity<MessageResponse> create(@RequestBody MessageCreateRequest request) {
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
}