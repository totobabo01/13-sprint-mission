package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// BinaryContent 데이터를 파일에 저장하고 조회하는 Repository 구현체
// 객체 직렬화를 사용해서 Map<UUID, BinaryContent> 형태로 파일에 저장
@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = true
)
public class FileBinaryContentRepository implements BinaryContentRepository {

    // BinaryContent 데이터를 저장할 파일 경로
    private final Path filePath;

    // 추가한 부분:
    // Spring 자동 Bean 등록용 생성자
    // 생성자가 여러 개 있을 때 Spring이 이 생성자를 사용하도록 @Autowired를 붙임
    @Autowired
    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Path.of(fileDirectory, "spring-binary-contents.ser");
    }

    // 기존 생성자 유지:
    // JavaApplication에서 직접 new FileBinaryContentRepository(Path.of(...)) 할 때 사용 가능
    public FileBinaryContentRepository(Path filePath) {
        this.filePath = filePath;
    }

    // 파일에서 BinaryContent 데이터를 읽어오는 메서드
    // 파일이 없으면 빈 HashMap을 반환
    @SuppressWarnings("unchecked")
    private Map<UUID, BinaryContent> loadData() {
        if (!Files.exists(filePath)) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Map<UUID, BinaryContent>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("바이너리 콘텐츠 데이터 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // BinaryContent 데이터를 파일에 저장하는 메서드
    private void saveData(Map<UUID, BinaryContent> data) {
        try {
            // filePath.getParent()가 null일 수 있으므로 null이 아닐 때만 폴더 생성
            createParentDirectoryIfNeeded();

            // Map 전체를 직렬화해서 파일에 저장
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("바이너리 콘텐츠 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    // 부모 폴더가 있는 경우에만 생성하는 보조 메서드
    private void createParentDirectoryIfNeeded() {
        Path parent = filePath.getParent();

        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                throw new RuntimeException("바이너리 콘텐츠 데이터 파일의 상위 폴더를 생성하는 중 오류가 발생했습니다.", e);
            }
        }
    }

    // BinaryContent 저장
    // 새 BinaryContent면 추가, 같은 id가 있으면 덮어쓰기
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Map<UUID, BinaryContent> data = loadData();

        UUID id = binaryContent.getId();
        data.put(id, binaryContent);

        saveData(data);
        return binaryContent;
    }

    // id로 BinaryContent 단건 조회
    @Override
    public BinaryContent findById(UUID id) {
        Map<UUID, BinaryContent> data = loadData();

        return data.get(id);
    }

    // 전체 BinaryContent 조회
    @Override
    public List<BinaryContent> findAll() {
        Map<UUID, BinaryContent> data = loadData();

        return new ArrayList<>(data.values());
    }

    // id로 BinaryContent 삭제
    @Override
    public void deleteById(UUID id) {
        Map<UUID, BinaryContent> data = loadData();

        data.remove(id);

        saveData(data);
    }

    // id에 해당하는 BinaryContent가 존재하는지 확인
    @Override
    public boolean existsById(UUID id) {
        Map<UUID, BinaryContent> data = loadData();

        return data.containsKey(id);
    }

    // 여러 BinaryContent id를 한 번에 조회
    // Message의 attachmentIds를 실제 BinaryContent 목록으로 조회할 때 사용
    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        Map<UUID, BinaryContent> data = loadData();

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