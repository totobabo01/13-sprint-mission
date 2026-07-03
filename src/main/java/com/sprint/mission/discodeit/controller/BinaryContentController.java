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
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // BinaryContent 생성
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

    // BinaryContent 단건 메타데이터 조회
    // GET /api/binary-contents/{binaryContentId}
    // GET /api/binaryContents/{binaryContentId}
    //
    // 주의:
    // 이 API는 파일 bytes를 내려주지 않고,
    // 파일명, contentType, size 같은 메타데이터만 반환한다.
    @GetMapping({
            "/api/binary-contents/{binaryContentId}",
            "/api/binaryContents/{binaryContentId}"
    })
    public ResponseEntity<BinaryContentResponse> find(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentResponse response = binaryContentService.find(binaryContentId);

        return ResponseEntity.ok(response);
    }

    // BinaryContent 여러 개 메타데이터 조회
    // GET /api/binary-contents?binaryContentIds=uuid1&binaryContentIds=uuid2
    // GET /api/binaryContents?binaryContentIds=uuid1&binaryContentIds=uuid2
    //
    // 기존 ids 대신 제공 API 스펙에 가까운 binaryContentIds 사용
    @GetMapping({
            "/api/binary-contents",
            "/api/binaryContents"
    })
    public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentResponse> responses =
                binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(responses);
    }

    // BinaryContent 실제 파일 다운로드/조회
    // GET /api/binary-contents/{binaryContentId}/download
    // GET /api/binaryContents/{binaryContentId}/download
    //
    // 이 API는 JSON이 아니라 실제 byte[] 파일 응답을 반환한다.
    @GetMapping({
            "/api/binary-contents/{binaryContentId}/download",
            "/api/binaryContents/{binaryContentId}/download"
    })
    public ResponseEntity<byte[]> download(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDownloadResponse response =
                binaryContentService.findForDownload(binaryContentId);

        return toFileResponse(response);
    }

    // 기존 심화 요구사항/프론트 호환용 파일 조회
    // GET /api/binaryContent/find?binaryContentId=...
    //
    // 기존 프론트 호환을 위해 유지하되,
    // 역할은 실제 파일 byte[] 응답으로 명확히 한다.
    @GetMapping("/api/binaryContent/find")
    public ResponseEntity<byte[]> findByRequestParam(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContentDownloadResponse response =
                binaryContentService.findForDownload(binaryContentId);

        return toFileResponse(response);
    }

    // BinaryContent 삭제
    // DELETE /api/binary-contents/{binaryContentId}
    // DELETE /api/binaryContents/{binaryContentId}
    @DeleteMapping({
            "/api/binary-contents/{binaryContentId}",
            "/api/binaryContents/{binaryContentId}"
    })
    public ResponseEntity<Void> delete(
            @PathVariable UUID binaryContentId
    ) {
        binaryContentService.delete(binaryContentId);

        return ResponseEntity
                .noContent()
                .build();
    }

    private ResponseEntity<byte[]> toFileResponse(
            BinaryContentDownloadResponse response
    ) {
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
}