package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    // PUBLIC 채널 생성
    @PostMapping("/public")
    public ChannelResponse createPublicChannel(@RequestBody ChannelCreateRequest request) {
        return channelService.createPublicChannel(request);
    }

    // PRIVATE 채널 생성
    @PostMapping("/private")
    public ChannelResponse createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        return channelService.createPrivateChannel(request);
    }

    // 채널 단건 조회
    @GetMapping("/{channelId}")
    public ChannelResponse find(@PathVariable UUID channelId) {
        return channelService.find(channelId);
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    @GetMapping
    public List<ChannelResponse> findAllByUserId(@RequestParam UUID userId) {
        return channelService.findAllByUserId(userId);
    }

    // 채널 수정
    @PatchMapping
    public ChannelResponse update(@RequestBody ChannelUpdateRequest request) {
        return channelService.update(request);
    }

    // 채널 삭제
    @DeleteMapping("/{channelId}")
    public void delete(@PathVariable UUID channelId) {
        channelService.delete(channelId);
    }
}