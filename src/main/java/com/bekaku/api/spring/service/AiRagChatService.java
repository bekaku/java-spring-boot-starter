package com.bekaku.api.spring.service;

import com.bekaku.api.spring.dto.ChatRequest;
import com.bekaku.api.spring.dto.ChatStreamEvent;
import reactor.core.publisher.Flux;

public interface AiRagChatService {
    Flux<ChatStreamEvent> streamAnswer(Long userId, ChatRequest request);
}
