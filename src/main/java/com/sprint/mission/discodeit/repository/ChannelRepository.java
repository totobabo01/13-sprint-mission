package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf"
)
public interface ChannelRepository {
    Channel save(Channel channel);

    Channel findById(UUID id);

    List<Channel> findAll();

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
