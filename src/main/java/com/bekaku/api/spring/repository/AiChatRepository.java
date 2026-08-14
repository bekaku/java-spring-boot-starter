package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.AiChat;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AiChatRepository extends BaseRepository<AiChat,Long>, JpaSpecificationExecutor<AiChat> {

    @Modifying
    @Query("UPDATE AiChat e SET e.updatedDate = ?2 WHERE e.id = ?1")
    void updateLatestUpdateDate(Long chatId, LocalDateTime updatedDate);
}
