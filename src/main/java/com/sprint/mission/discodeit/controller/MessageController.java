package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 생성
    @PostMapping
    public MessageResponse create(@RequestBody MessageCreateRequest request) {
        return messageService.create(request);
    }

    // 메시지 단건 조회
    @GetMapping("/{messageId}")
    public MessageResponse read(@PathVariable UUID messageId) {
        return messageService.read(messageId);
    }

    // 특정 채널의 메시지 목록 조회
    @GetMapping
    public List<MessageResponse> findAllByChannelId(@RequestParam UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    // 메시지 수정
    @PatchMapping
    public MessageResponse update(@RequestBody MessageUpdateRequest request) {
        return messageService.update(request);
    }

    // 메시지 삭제
    @DeleteMapping("/{messageId}")
    public void delete(@PathVariable UUID messageId) {
        messageService.delete(messageId);
    }
}