package com.bekaku.api.spring.service;
import com.bekaku.api.spring.dto.AiChatDto;

import com.bekaku.api.spring.model.AiChat;

import java.time.LocalDateTime;

public interface AiChatService extends BaseService<AiChat, AiChatDto> {

    void updateLatestUpdateDate(Long chatId, LocalDateTime updatedDate);
}
