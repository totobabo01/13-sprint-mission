package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 생성 기능
    // 수정한 부분: content, authorId, channelId를 따로 받지 않고 MessageCreateRequest DTO로 받음
    MessageResponse create(MessageCreateRequest request);

    // 메시지 한 개 읽기 기능
    // 수정한 부분: Message 엔티티가 아니라 MessageResponse DTO 반환
    MessageResponse read(UUID id);

    // 메시지 전체 읽기 기능
    // 수정한 부분: List<Message>가 아니라 List<MessageResponse> 반환
    List<MessageResponse> readAll();

    // 메시지 수정 기능
    // 수정한 부분: id, content를 따로 받지 않고 MessageUpdateRequest DTO로 받음
    MessageResponse update(MessageUpdateRequest request);

    // 메시지 삭제 기능
    void delete(UUID id);
}