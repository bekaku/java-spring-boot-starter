package com.bekaku.api.spring.repository;

import com.bekaku.api.spring.model.AiChat;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AiChatRepository extends BaseRepository<AiChat,Long>, JpaSpecificationExecutor<AiChat> {

    @Modifying
    @Query("UPDATE AiChat e SET e.updatedDate = ?2 WHERE e.id = ?1")
    void updateLatestUpdateDate(Long chatId, LocalDateTime updatedDate);

    @Modifying
    @Query("UPDATE AiChat e SET e.title = ?2 WHERE e.id = ?1")
    void updateTitle(Long chatId, String title);

    @Query("SELECT e FROM AiChat e WHERE e.id = ?1 AND e.createdUser = ?2")
    Optional<AiChat> findByIdAndCreator(Long chatId, Long creatorId);
}
