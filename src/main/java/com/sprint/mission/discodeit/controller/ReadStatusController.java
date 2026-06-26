package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/read-statuses")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // ReadStatus 생성
    @PostMapping
    public ResponseEntity<ReadStatusResponse> create(
            @RequestBody ReadStatusCreateRequest request
    ) {
        ReadStatusResponse response = readStatusService.create(request);

        URI location = URI.create("/api/read-statuses/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // ReadStatus 단건 조회
    @GetMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> find(
            @PathVariable UUID readStatusId
    ) {
        ReadStatusResponse response = readStatusService.find(readStatusId);

        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 모든 ReadStatus 조회
    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        List<ReadStatusResponse> responses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(responses);
    }

    // ReadStatus 수정
    @PatchMapping
    public ResponseEntity<ReadStatusResponse> update(
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusResponse response = readStatusService.update(request);

        return ResponseEntity.ok(response);
    }

    // ReadStatus 삭제
    @DeleteMapping("/{readStatusId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID readStatusId
    ) {
        readStatusService.delete(readStatusId);

        return ResponseEntity
                .noContent()
                .build();
    }
}