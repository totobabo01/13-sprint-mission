package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf"
)
// BinaryContent 데이터를 메모리에 저장하고 조회하는 Repository 구현체
// JCF의 HashMap을 사용해서 데이터를 저장함
public class JCFBinaryContentRepository implements BinaryContentRepository {

    // BinaryContent 데이터를 저장하는 Map
    // key: BinaryContent id
    // value: BinaryContent 객체
    private final Map<UUID, BinaryContent> data;

    // 생성자: BinaryContent 데이터를 저장할 HashMap 초기화
    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    // BinaryContent 저장
    // 새 BinaryContent면 추가되고, 같은 id가 있으면 덮어쓰기됨
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        UUID id = binaryContent.getId();
        data.put(id, binaryContent);

        return binaryContent;
    }

    // id로 BinaryContent 한 개 조회
    // 해당 id의 BinaryContent가 없으면 null 반환
    @Override
    public BinaryContent findById(UUID id) {
        return data.get(id);
    }

    // 저장된 모든 BinaryContent 조회
    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    // id로 BinaryContent 삭제
    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    // 해당 id의 BinaryContent가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    // 추가한 부분:
    // 여러 BinaryContent id를 한 번에 조회
    // Message의 attachmentIds를 실제 BinaryContent 목록으로 조회할 때 사용
    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();

        if (ids == null || ids.isEmpty()) {
            return result;
        }

        for (UUID id : ids) {
            BinaryContent binaryContent = data.get(id);

            if (binaryContent != null) {
                result.add(binaryContent);
            }
        }

        return result;
    }
}