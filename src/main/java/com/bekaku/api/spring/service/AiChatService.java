package com.bekaku.api.spring.service;
import com.bekaku.api.spring.dto.AiChatDto;

import com.bekaku.api.spring.model.AiChat;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AiChatService extends BaseService<AiChat, AiChatDto> {

    Optional<AiChat> findByIdAndCreator(Long chatId, Long creatorId);
    void updateLatestUpdateDate(Long chatId, LocalDateTime updatedDate);
    void updateTitle(Long chatId, String title);
    boolean existsById(Long id);
}
