package com.bekaku.api.spring.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.bekaku.api.spring.model.AiChatMessage;

import java.util.List;

@Repository
public interface AiChatMessageRepository extends BaseRepository<AiChatMessage,Long>, JpaSpecificationExecutor<AiChatMessage> {

    @Query(value = """
            SELECT * FROM (
                SELECT * FROM ai_chat_messages 
                WHERE ai_chat = ?1 
                ORDER BY created_date DESC 
                LIMIT ?2
            ) sub 
            ORDER BY sub.created_date ASC
            """, nativeQuery = true)
    List<AiChatMessage> findLastNMessagesByChatId(Long chatId, int limit);

    List<AiChatMessage> findByAiChatIdOrderByCreatedDateDesc(Long chatId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM AiChatMessage e WHERE e.aiChat.id = ?1")
    void deleteByAiChatId(Long chatId);
}
