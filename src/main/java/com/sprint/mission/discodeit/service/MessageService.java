package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 메시지 생성 기능
    public Message create(String content, UUID authorId, UUID channelId);
    // 메시지 한 개 읽기 기능
    public Message read(UUID id);
    // 메시지 전체 읽기 기능
    public List<Message> readAll();
    // 메시지 수정 기능
    public Message update(UUID id, String content);
    // 메시지 삭제 기능
    public void delete(UUID id);
}
