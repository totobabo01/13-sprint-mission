package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

    // 파일 이름
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    // 파일 타입
    // 예: image/png, image/jpeg, application/pdf
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    /*
     * 실제 바이너리 데이터
     *
     * PostgreSQL의 BYTEA 컬럼과 매핑한다.
     * @Lob을 사용하면 Hibernate가 Large Object 방식으로 처리하면서
     * bytea 컬럼에 bigint 값을 넣으려는 오류가 발생할 수 있다.
     */
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "bytes", nullable = false, columnDefinition = "bytea")
    private byte[] bytes;

    // 파일 크기
    @Column(name = "size", nullable = false)
    private Long size;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        validate(fileName, contentType, bytes);

        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = (long) bytes.length;
    }

    /*
     * BinaryContent는 BaseEntity만 상속하므로 updatedAt을 실제 필드로 가지지 않는다.
     * 기존 BinaryContentResponse 생성 코드 호환용으로 null을 반환한다.
     */
    public Instant getUpdatedAt() {
        return null;
    }

    private void validate(String fileName, String contentType, byte[] bytes) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("파일 이름은 비어 있을 수 없습니다.");
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("파일 타입은 비어 있을 수 없습니다.");
        }

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("파일 데이터는 비어 있을 수 없습니다.");
        }
    }
}