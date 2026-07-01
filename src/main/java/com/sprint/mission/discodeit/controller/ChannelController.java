package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
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
    // 수정됨: 프론트에서 type을 보내지 않아도 PUBLIC으로 보정해서 서비스에 전달
    @PostMapping("/public")
    public ResponseEntity<ChannelResponse> createPublicChannel(
            @RequestBody ChannelCreateRequest request
    ) {
        ChannelCreateRequest fixedRequest = new ChannelCreateRequest(
                ChannelType.PUBLIC,
                request.getName(),
                request.getDescription()
        );

        ChannelResponse response = channelService.createPublicChannel(fixedRequest);

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

    // 수정됨: 프론트가 PATCH /api/channels/{channelId} 방식으로 보낼 가능성 대비
    // request body 안에 id가 없더라도 URL의 channelId를 사용해서 수정 가능하게 보정
    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponse> updateByPathVariable(
            @PathVariable UUID channelId,
            @RequestBody ChannelUpdateRequest request
    ) {
        ChannelUpdateRequest fixedRequest = new ChannelUpdateRequest(
                channelId,
                request.getType(),
                request.getName(),
                request.getDescription()
        );

        ChannelResponse response = channelService.update(fixedRequest);

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