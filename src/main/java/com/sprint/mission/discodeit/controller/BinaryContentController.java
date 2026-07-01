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
// 수정됨: class 레벨 @RequestMapping 제거
// 이유:
// 1. /api/binary-contents/{id}      → 기존 RESTful 메타데이터 조회
// 2. /api/binaryContents/{id}       → 프론트 이미지 표시용 JSON 조회
// 3. /api/binaryContent/find?...    → 실제 파일 byte[] 다운로드
// 경로별 응답 형태가 달라서 메서드마다 전체 경로를 직접 지정
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 기존 방식: BinaryContent 생성
    // POST /api/binary-contents
    // POST /api/binaryContents
    @PostMapping({"/api/binary-contents", "/api/binaryContents"})
    public ResponseEntity<BinaryContentResponse> create(
            @RequestBody BinaryContentCreateRequest request
    ) {
        BinaryContentResponse response = binaryContentService.create(request);

        URI location = URI.create("/api/binary-contents/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // 기존 방식: BinaryContent 단건 메타데이터 조회(JSON)
    // GET /api/binary-contents/{binaryContentId}
    @GetMapping("/api/binary-contents/{binaryContentId}")
    public ResponseEntity<BinaryContentResponse> find(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentResponse response = binaryContentService.find(binaryContentId);

        return ResponseEntity.ok(response);
    }

    // 수정됨: 프론트엔드 프로필 이미지 조회용 경로
    // GET /api/binaryContents/{binaryContentId}
    //
    // 중요:
    // 이전에는 byte[]를 바로 내려줬지만,
    // 현재 프론트는 contentType, bytes 값을 이용해
    // data:image/png;base64,... 형태를 만들려고 함.
    //
    // 따라서 byte[] 파일 응답이 아니라 JSON 응답으로 내려줘야 함.
    @GetMapping("/api/binaryContents/{binaryContentId}")
    public ResponseEntity<BinaryContentDownloadResponse> findForFrontend(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDownloadResponse response = binaryContentService.findForDownload(binaryContentId);

        return ResponseEntity.ok(response);
    }

    // 기존 심화 요구사항 방식: BinaryContent 실제 파일 조회
    // GET /api/binaryContent/find?binaryContentId=...
    //
    // 이 경로는 실제 파일 데이터를 직접 내려주는 다운로드/이미지 표시용 byte[] 응답으로 유지
    @GetMapping("/api/binaryContent/find")
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
    // GET /api/binary-contents?ids=uuid1&ids=uuid2
    // GET /api/binaryContents?ids=uuid1&ids=uuid2
    @GetMapping({"/api/binary-contents", "/api/binaryContents"})
    public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
            @RequestParam List<UUID> ids
    ) {
        List<BinaryContentResponse> responses = binaryContentService.findAllByIdIn(ids);

        return ResponseEntity.ok(responses);
    }

    // BinaryContent 삭제
    // DELETE /api/binary-contents/{binaryContentId}
    // DELETE /api/binaryContents/{binaryContentId}
    @DeleteMapping({"/api/binary-contents/{binaryContentId}", "/api/binaryContents/{binaryContentId}"})
    public ResponseEntity<Void> delete(
            @PathVariable UUID binaryContentId
    ) {
        binaryContentService.delete(binaryContentId);

        return ResponseEntity
                .noContent()
                .build();
    }
}