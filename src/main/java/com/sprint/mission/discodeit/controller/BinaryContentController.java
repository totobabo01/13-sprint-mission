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
    @PostMapping({
            "/api/binary-contents",
            "/api/binaryContents"
    })
    public ResponseEntity<BinaryContentResponse> create(
            @RequestBody BinaryContentCreateRequest request
    ) {
        BinaryContentResponse response = binaryContentService.create(request);

        URI location = URI.create("/api/binaryContents/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    /*
     * 파일 메타데이터 조회
     *
     * 중요:
     * /api/binaryContents/{id} 에서는 실제 이미지 byte[]가 아니라
     * contentType, fileName, size 등이 담긴 JSON을 내려줘야 한다.
     *
     * 프론트가 이 응답에서 contentType을 읽고 startsWith("image/")를 호출한다.
     */
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

    /*
     * BinaryContent 여러 개 메타데이터 조회
     *
     * GET /api/binary-contents?binaryContentIds=uuid1&binaryContentIds=uuid2
     * GET /api/binaryContents?binaryContentIds=uuid1&binaryContentIds=uuid2
     */
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

    /*
     * 실제 이미지/파일 조회 경로
     *
     * 이미지 표시나 파일 다운로드는 반드시 /download 경로에서 처리한다.
     *
     * GET /api/binary-contents/{id}/download
     * GET /api/binaryContents/{id}/download
     */
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

    /*
     * 기존 심화 요구사항/구버전 호환용
     *
     * 이 경로는 실제 파일 byte[] 반환용으로 유지
     */
    @GetMapping("/api/binaryContent/find")
    public ResponseEntity<byte[]> findByRequestParam(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContentDownloadResponse response =
                binaryContentService.findForDownload(binaryContentId);

        return toFileResponse(response);
    }

    // BinaryContent 삭제
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
        String contentType = response.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(response.getSize())
                .headers(headers -> headers.setContentDisposition(
                        ContentDisposition.inline()
                                .filename(response.getFileName(), StandardCharsets.UTF_8)
                                .build()
                ))
                .body(response.getBytes());
    }
}