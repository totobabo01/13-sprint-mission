package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentDownloadResponse {

    private UUID id;

    private String fileName;

    private String contentType;

    private byte[] bytes;

    private Long size;

    public BinaryContentDownloadResponse(
            UUID id,
            String fileName,
            String contentType,
            byte[] bytes,
            Long size
    ) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = size;
    }
}