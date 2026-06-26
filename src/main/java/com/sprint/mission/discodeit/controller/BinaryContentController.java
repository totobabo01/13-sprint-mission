package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDownloadResponse;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/binary-contents", "/api/binaryContent"})
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // BinaryContent 생성
    @PostMapping
    public ResponseEntity<BinaryContentResponse> create(@RequestBody BinaryContentCreateRequest request) {
        BinaryContentResponse response = binaryContentService.create(request);

        URI location = URI.create("/api/binary-contents/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // 기존 방식: BinaryContent 단건 메타데이터 조회(JSON)
    // GET /api/binary-contents/{binaryContentId}
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentResponse> find(@PathVariable UUID binaryContentId) {
        BinaryContentResponse response = binaryContentService.find(binaryContentId);

        return ResponseEntity.ok(response);
    }

    // 심화 요구사항 방식: BinaryContent 실제 파일 조회
    // GET /api/binaryContent/find?binaryContentId=...
    @GetMapping("/find")
    public ResponseEntity<byte[]> findByRequestParam(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContentDownloadResponse response = binaryContentService.findForDownload(binaryContentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .contentLength(response.getSize())
                .headers(headers -> headers.setContentDisposition(
                        ContentDisposition.inline()
                                .filename(response.getFileName(), StandardCharsets.UTF_8)
                                .build()
                ))
                .body(response.getBytes());
    }

    // 여러 BinaryContent 메타데이터 조회(JSON)
    // 예: /api/binary-contents?ids=uuid1&ids=uuid2
    @GetMapping
    public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
            @RequestParam List<UUID> ids
    ) {
        List<BinaryContentResponse> responses = binaryContentService.findAllByIdIn(ids);

        return ResponseEntity.ok(responses);
    }

    // BinaryContent 삭제
    @DeleteMapping("/{binaryContentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID binaryContentId) {
        binaryContentService.delete(binaryContentId);

        return ResponseEntity
                .noContent()
                .build();
    }
}