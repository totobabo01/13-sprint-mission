package com.sprint.mission.discodeit.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageMultipartMapper {

    private final ObjectMapper objectMapper;

    public MessageCreateRequest toCreateRequest(
            MultiValueMap<String, String> formData,
            String messageCreateRequestJson,
            String requestJson,
            String messageRequestJson,
            List<MultipartFile> attachments,
            List<MultipartFile> files
    ) throws IOException {
        String content = firstNonBlank(
                getFirst(formData, "content"),
                getFirst(formData, "body"),
                getFirst(formData, "text"),
                getFirst(formData, "message")
        );

        UUID authorId = firstNonNull(
                getUuid(getFirst(formData, "authorId")),
                getUuid(getFirst(formData, "userId")),
                getUuid(getFirst(formData, "senderId"))
        );

        UUID channelId = firstNonNull(
                getUuid(getFirst(formData, "channelId")),
                getUuid(getFirst(formData, "roomId"))
        );

        String json = firstNonBlank(
                messageCreateRequestJson,
                requestJson,
                messageRequestJson,
                getFirst(formData, "messageCreateRequest"),
                getFirst(formData, "request"),
                getFirst(formData, "messageRequest")
        );

        if (!isBlank(json)) {
            JsonNode root = objectMapper.readTree(json);

            content = firstNonBlank(
                    content,
                    getText(root, "content"),
                    getText(root, "body"),
                    getText(root, "text"),
                    getText(root, "message")
            );

            authorId = firstNonNull(
                    authorId,
                    getUuid(getText(root, "authorId")),
                    getUuid(getText(root, "userId")),
                    getUuid(getText(root, "senderId"))
            );

            channelId = firstNonNull(
                    channelId,
                    getUuid(getText(root, "channelId")),
                    getUuid(getText(root, "roomId"))
            );
        }

        List<BinaryContentCreateRequest> attachmentRequests =
                toBinaryContentCreateRequests(firstNonEmptyList(attachments, files));

        return new MessageCreateRequest(
                content,
                authorId,
                channelId,
                attachmentRequests
        );
    }

    private List<BinaryContentCreateRequest> toBinaryContentCreateRequests(
            List<MultipartFile> files
    ) throws IOException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<BinaryContentCreateRequest> requests = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );

            requests.add(request);
        }

        return requests;
    }

    private List<MultipartFile> firstNonEmptyList(
            List<MultipartFile> first,
            List<MultipartFile> second
    ) {
        if (first != null && !first.isEmpty()) {
            return first;
        }

        if (second != null && !second.isEmpty()) {
            return second;
        }

        return List.of();
    }

    private String getFirst(MultiValueMap<String, String> formData, String key) {
        if (formData == null) {
            return null;
        }

        return formData.getFirst(key);
    }

    private String getText(JsonNode root, String fieldName) {
        if (root == null || fieldName == null || !root.has(fieldName)) {
            return null;
        }

        JsonNode node = root.get(fieldName);

        if (node == null || node.isNull()) {
            return null;
        }

        return node.asText();
    }

    private UUID getUuid(String value) {
        if (isBlank(value)) {
            return null;
        }

        return UUID.fromString(value);
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }

        return null;
    }

    @SafeVarargs
    private final <T> T firstNonNull(T... values) {
        if (values == null) {
            return null;
        }

        for (T value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}