package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// BinaryContent 데이터를 저장하고 조회하기 위한 Repository 인터페이스
// BinaryContent는 프로필 이미지, 메시지 첨부파일 같은 바이너리 파일 정보를 나타냄
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf"
)
public interface BinaryContentRepository {

    // BinaryContent 저장
    // 새 파일 정보 생성 또는 기존 파일 정보 수정 후 저장할 때 사용
    BinaryContent save(BinaryContent binaryContent);

    // id로 BinaryContent 한 개 조회
    BinaryContent findById(UUID id);

    // 모든 BinaryContent 조회
    List<BinaryContent> findAll();

    // id로 BinaryContent 삭제
    void deleteById(UUID id);

    // 해당 id를 가진 BinaryContent가 존재하는지 확인
    boolean existsById(UUID id);

    // 추가한 부분:
    // 여러 BinaryContent id를 한 번에 조회
    // Message 첨부파일 id 목록을 실제 BinaryContent 목록으로 조회할 때 사용
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
}