package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // ReadStatus 생성
    @PostMapping
    public ReadStatusResponse create(@RequestBody ReadStatusCreateRequest request) {
        return readStatusService.create(request);
    }

    // ReadStatus 단건 조회
    @GetMapping("/{readStatusId}")
    public ReadStatusResponse find(@PathVariable UUID readStatusId) {
        return readStatusService.find(readStatusId);
    }

    // 특정 사용자의 모든 ReadStatus 조회
    @GetMapping
    public List<ReadStatusResponse> findAllByUserId(@RequestParam UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }

    // ReadStatus 수정
    @PatchMapping
    public ReadStatusResponse update(@RequestBody ReadStatusUpdateRequest request) {
        return readStatusService.update(request);
    }

    // ReadStatus 삭제
    @DeleteMapping("/{readStatusId}")
    public void delete(@PathVariable UUID readStatusId) {
        readStatusService.delete(readStatusId);
    }
}