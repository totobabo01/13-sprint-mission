package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/binary-contents", "/api/binaryContent"})
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // BinaryContent 생성
    @PostMapping
    public BinaryContentResponse create(@RequestBody BinaryContentCreateRequest request) {
        return binaryContentService.create(request);
    }

    // 기존 방식: BinaryContent 단건 조회
    // GET /api/binary-contents/{binaryContentId}
    @GetMapping("/{binaryContentId}")
    public BinaryContentResponse find(@PathVariable UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }

    // 심화 요구사항 방식: BinaryContent 단건 조회
    // GET /api/binaryContent/find?binaryContentId=...
    @GetMapping("/find")
    public BinaryContentResponse findByRequestParam(@RequestParam UUID binaryContentId) {
        return binaryContentService.find(binaryContentId);
    }

    // 여러 BinaryContent 조회
    // 예: /api/binary-contents?ids=uuid1&ids=uuid2
    @GetMapping
    public List<BinaryContentResponse> findAllByIdIn(@RequestParam List<UUID> ids) {
        return binaryContentService.findAllByIdIn(ids);
    }

    // BinaryContent 삭제
    @DeleteMapping("/{binaryContentId}")
    public void delete(@PathVariable UUID binaryContentId) {
        binaryContentService.delete(binaryContentId);
    }
}