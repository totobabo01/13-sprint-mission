package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    // 데이터 필드
    private final Map<UUID, Message> data;
    private final UserService udata;
    private final ChannelService cdata;

    // 생성자
    public JCFMessageService(UserService udata, ChannelService cdata) {
        data = new HashMap<>();
        this.udata = udata;
        this.cdata = cdata;
    }

    @Override
    public Message create(String content, UUID authorId, UUID channelId) {
        User author = udata.read(authorId);
        Channel channel =  cdata.read(channelId);
        if(author == null || channel == null) {
            return null;
        }

        Message message = new Message(content, authorId, channelId);
        UUID id = message.getId();
        data.put(id, message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        Message readMessage = data.get(id);
        if (readMessage == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지 id입니다.");
        }
        return readMessage;
    }

    @Override
    public List<Message> readAll() {
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(data.values());
        return allMessages;
    }

    @Override
    public Message update(UUID id, String content) {
        Message updateMessage = data.get(id);
        if (updateMessage == null) {
            throw new IllegalArgumentException("수정된 메시지 정보를 조회할 수 없습니다.");
        }
        updateMessage.update(content);
        return updateMessage;
    }

    @Override
    public void delete(UUID id) {
        if(data.get(id) == null) {
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }

        data.remove(id);
    }
}
