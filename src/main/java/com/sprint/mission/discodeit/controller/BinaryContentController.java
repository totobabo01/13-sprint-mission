package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDownloadResponse;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.validation.Valid;
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
@RequestMapping("/api")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    /*
     * 기존 테스트 및 Postman 호환을 위한 BinaryContent 생성 API
     */
    @PostMapping({
            "/binaryContents",
            "/binary-contents"
    })
    public ResponseEntity<BinaryContentResponse> create(
            @Valid @RequestBody BinaryContentCreateRequest request
    ) {
        BinaryContentResponse response =
                binaryContentService.create(request);

        return ResponseEntity
                .created(URI.create("/api/binaryContents/" + response.getId()))
                .body(response);
    }

    /*
     * BinaryContent 메타데이터 단건 조회
     */
    @GetMapping({
            "/binaryContents/{binaryContentId}",
            "/binary-contents/{binaryContentId}"
    })
    public ResponseEntity<BinaryContentResponse> find(
            @PathVariable UUID binaryContentId
    ) {
        return ResponseEntity.ok(
                binaryContentService.find(binaryContentId)
        );
    }

    /*
     * BinaryContent 메타데이터 목록 조회
     *
     * 요청 예시:
     * GET /api/binaryContents
     *     ?binaryContentIds=uuid1
     *     &binaryContentIds=uuid2
     */
    @GetMapping({
            "/binaryContents",
            "/binary-contents"
    })
    public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
            @RequestParam List<UUID> binaryContentIds
    ) {
        return ResponseEntity.ok(
                binaryContentService.findAllByIdIn(binaryContentIds)
        );
    }

    /*
     * 실제 파일 데이터 다운로드
     */
    @GetMapping({
            "/binaryContents/{binaryContentId}/download",
            "/binary-contents/{binaryContentId}/download"
    })
    public ResponseEntity<byte[]> download(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDownloadResponse response =
                binaryContentService.findForDownload(binaryContentId);

        return ResponseEntity.ok()
                .contentType(resolveMediaType(response.getContentType()))
                .contentLength(response.getSize())
                .headers(headers -> headers.setContentDisposition(
                        ContentDisposition.inline()
                                .filename(
                                        resolveFileName(response.getFileName()),
                                        StandardCharsets.UTF_8
                                )
                                .build()
                ))
                .body(response.getBytes());
    }

    /*
     * 구버전 프론트 호환용 파일 다운로드 API
     */
    @GetMapping("/binaryContent/find")
    public ResponseEntity<byte[]> findByRequestParam(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContentDownloadResponse response =
                binaryContentService.findForDownload(binaryContentId);

        return ResponseEntity.ok()
                .contentType(resolveMediaType(response.getContentType()))
                .contentLength(response.getSize())
                .headers(headers -> headers.setContentDisposition(
                        ContentDisposition.inline()
                                .filename(
                                        resolveFileName(response.getFileName()),
                                        StandardCharsets.UTF_8
                                )
                                .build()
                ))
                .body(response.getBytes());
    }

    /*
     * 기존 기능 호환을 위한 BinaryContent 삭제 API
     */
    @DeleteMapping({
            "/binaryContents/{binaryContentId}",
            "/binary-contents/{binaryContentId}"
    })
    public ResponseEntity<Void> delete(
            @PathVariable UUID binaryContentId
    ) {
        binaryContentService.delete(binaryContentId);

        return ResponseEntity.noContent().build();
    }

    /*
     * 파일 응답을 만드는 데 필요한 HTTP 표현 계층 처리
     */
    private MediaType resolveMediaType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        return MediaType.parseMediaType(contentType);
    }

    private String resolveFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "download";
        }

        return fileName;
    }
}