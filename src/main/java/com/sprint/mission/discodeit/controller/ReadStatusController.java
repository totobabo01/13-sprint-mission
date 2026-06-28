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
// 수정됨: 기존 RESTful 경로(/api/read-statuses)는 유지하고,
// 프론트엔드가 요청하는 camelCase 경로(/api/readStatuses)도 함께 허용
@RequestMapping({"/api/read-statuses", "/api/readStatuses"})
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // ReadStatus 생성
    // POST /api/read-statuses
    // POST /api/readStatuses
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
    // GET /api/read-statuses/{readStatusId}
    // GET /api/readStatuses/{readStatusId}
    @GetMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> find(
            @PathVariable UUID readStatusId
    ) {
        ReadStatusResponse response = readStatusService.find(readStatusId);

        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 모든 ReadStatus 조회
    // GET /api/read-statuses?userId=...
    // GET /api/readStatuses?userId=...
    @GetMapping
    public ResponseEntity<List<ReadStatusResponse>> findAllByUserId(
            @RequestParam UUID userId
    ) {
        List<ReadStatusResponse> responses = readStatusService.findAllByUserId(userId);

        return ResponseEntity.ok(responses);
    }

    // ReadStatus 수정
    // PATCH /api/read-statuses
    // PATCH /api/readStatuses
    // 기존 Postman/Swagger 테스트용 유지
    @PatchMapping
    public ResponseEntity<ReadStatusResponse> update(
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusResponse response = readStatusService.update(request);

        return ResponseEntity.ok(response);
    }

    // 수정됨: 프론트가 PATCH /api/readStatuses/{readStatusId} 로 요청하는 경우 처리
    // Body에 id가 없고 newLastActiveAt만 들어와도 URL의 readStatusId를 사용해서 수정
    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusResponse> updateByPathVariable(
            @PathVariable UUID readStatusId,
            @RequestBody ReadStatusUpdateRequest request
    ) {
        ReadStatusUpdateRequest fixedRequest = new ReadStatusUpdateRequest(
                readStatusId,
                request.getLastReadAt()
        );

        ReadStatusResponse response = readStatusService.update(fixedRequest);

        return ResponseEntity.ok(response);
    }

    // ReadStatus 삭제
    // DELETE /api/read-statuses/{readStatusId}
    // DELETE /api/readStatuses/{readStatusId}
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