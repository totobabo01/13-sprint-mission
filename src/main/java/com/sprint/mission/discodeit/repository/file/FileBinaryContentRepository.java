package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileBinaryContentRepository implements BinaryContentRepository {

    // BinaryContent 데이터를 저장할 파일 경로
    private final Path filePath;

    // 생성자: 저장 파일 경로를 외부에서 주입받음
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
            // data 폴더가 없으면 생성
            Files.createDirectories(filePath.getParent());

            // Map 전체를 직렬화해서 파일에 저장
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("바이너리 콘텐츠 데이터 파일을 저장하는 중 오류가 발생했습니다.", e);
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

        List<BinaryContent> allBinaryContents = new ArrayList<>();
        allBinaryContents.addAll(data.values());

        return allBinaryContents;
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
}