package com.bekaku.api.spring.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.AiChatMessage;

@Repository
public interface AiChatMessageRepository extends BaseRepository<AiChatMessage,Long>, JpaSpecificationExecutor<AiChatMessage> {
}
