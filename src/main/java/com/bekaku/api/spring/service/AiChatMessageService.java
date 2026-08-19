package com.bekaku.api.spring.service;
import com.bekaku.api.spring.dto.AiChatMessageDto;

import com.bekaku.api.spring.model.AiChatMessage;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AiChatMessageService extends BaseService<AiChatMessage, AiChatMessageDto> {

    List<AiChatMessage> findLastNMessagesByChatId(Long chatId, int limit);

    List<AiChatMessage> findByAiChatIdOrderByCreatedDateDesc(Long chatId, Pageable pageable);

    void deleteByAiChatId(Long chatId);
}
