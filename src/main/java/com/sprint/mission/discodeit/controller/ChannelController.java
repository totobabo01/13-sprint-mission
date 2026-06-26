package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // PUBLIC 채널 생성
    @PostMapping("/public")
    public ResponseEntity<ChannelResponse> createPublicChannel(
            @RequestBody ChannelCreateRequest request
    ) {
        ChannelResponse response = channelService.createPublicChannel(request);

        URI location = URI.create("/api/channels/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // PRIVATE 채널 생성
    @PostMapping("/private")
    public ResponseEntity<ChannelResponse> createPrivateChannel(
            @RequestBody PrivateChannelCreateRequest request
    ) {
        ChannelResponse response = channelService.createPrivateChannel(request);

        URI location = URI.create("/api/channels/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // 채널 단건 조회
    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> find(@PathVariable UUID channelId) {
        ChannelResponse response = channelService.find(channelId);

        return ResponseEntity.ok(response);
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    @GetMapping
    public ResponseEntity<List<ChannelResponse>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        List<ChannelResponse> responses = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(responses);
    }

    // 채널 수정
    @PatchMapping
    public ResponseEntity<ChannelResponse> update(
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelResponse response = channelService.update(request);

        return ResponseEntity.ok(response);
    }

    // 채널 삭제
    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);

        return ResponseEntity
                .noContent()
                .build();
    }
}