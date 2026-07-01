package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDownloadResponse;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("바이너리 콘텐츠 생성 요청은 비어 있을 수 없습니다.");
        }

        validate(request);

        BinaryContent binaryContent = new BinaryContent(
                request.getFileName(),
                request.getContentType(),
                request.getBytes()
        );

        binaryContentRepository.save(binaryContent);

        return toResponse(binaryContent);
    }

    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent binaryContent = findBinaryContentById(id);

        return toResponse(binaryContent);
    }

    @Override
    public BinaryContentDownloadResponse findForDownload(UUID id) {
        BinaryContent binaryContent = findBinaryContentById(id);

        return toDownloadResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAllByIdIn(List<UUID> ids) {
        List<BinaryContentResponse> responses = new ArrayList<>();

        if (ids == null || ids.isEmpty()) {
            return responses;
        }

        List<BinaryContent> binaryContents = binaryContentRepository.findAllByIdIn(ids);

        for (BinaryContent binaryContent : binaryContents) {
            responses.add(toResponse(binaryContent));
        }

        return responses;
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("삭제할 바이너리 콘텐츠 id는 null일 수 없습니다.");
        }

        if (!binaryContentRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 바이너리 콘텐츠를 찾을 수 없습니다.");
        }

        binaryContentRepository.deleteById(id);
    }

    private BinaryContent findBinaryContentById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 바이너리 콘텐츠 id는 null일 수 없습니다.");
        }

        BinaryContent binaryContent = binaryContentRepository.findById(id);

        if (binaryContent == null) {
            throw new IllegalArgumentException("조회할 바이너리 콘텐츠를 찾을 수 없습니다.");
        }

        return binaryContent;
    }

    private void validate(BinaryContentCreateRequest request) {
        if (request.getFileName() == null || request.getFileName().isBlank()) {
            throw new IllegalArgumentException("파일 이름은 비어 있을 수 없습니다.");
        }

        if (request.getContentType() == null || request.getContentType().isBlank()) {
            throw new IllegalArgumentException("파일 타입은 비어 있을 수 없습니다.");
        }

        if (request.getBytes() == null || request.getBytes().length == 0) {
            throw new IllegalArgumentException("파일 데이터는 비어 있을 수 없습니다.");
        }
    }

    // 목록/메타데이터 조회용 응답: bytes 제외
    private BinaryContentResponse toResponse(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getUpdatedAt(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getSize()
        );
    }

    // 실제 파일 다운로드용 응답: bytes 포함
    private BinaryContentDownloadResponse toDownloadResponse(BinaryContent binaryContent) {
        return new BinaryContentDownloadResponse(
                binaryContent.getId(),
                binaryContent.getFileName(),
                binaryContent.getContentType(),
                binaryContent.getBytes(),
                binaryContent.getSize()
        );
    }
}