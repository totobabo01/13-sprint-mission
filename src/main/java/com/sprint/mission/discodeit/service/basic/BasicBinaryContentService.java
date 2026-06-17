package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// BinaryContent 기능을 실제로 구현하는 Service 클래스
// BinaryContentService 인터페이스의 기능들을 구현함
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    // BinaryContent 데이터를 저장하고 조회하기 위한 Repository
    private final BinaryContentRepository binaryContentRepository;

    // BinaryContent 생성
    // 파일 이름, 파일 타입, 실제 바이트 데이터를 받아 BinaryContent를 생성함
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

    // id로 BinaryContent 단건 조회
    @Override
    public BinaryContentResponse find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id);

        if (binaryContent == null) {
            throw new IllegalArgumentException("조회할 바이너리 콘텐츠를 찾을 수 없습니다.");
        }

        return toResponse(binaryContent);
    }

    // 여러 id에 해당하는 BinaryContent 목록 조회
    // 메시지의 attachmentIds를 실제 첨부파일 응답 목록으로 변환할 때 사용
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

    // id로 BinaryContent 삭제
    @Override
    public void delete(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 바이너리 콘텐츠를 찾을 수 없습니다.");
        }

        binaryContentRepository.deleteById(id);
    }

    // BinaryContent 생성 요청 검증
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

    // BinaryContent 엔티티를 BinaryContentResponse DTO로 변환하는 보조 메서드
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
}