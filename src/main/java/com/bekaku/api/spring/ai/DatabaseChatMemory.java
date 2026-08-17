package com.bekaku.api.spring.ai;


import com.bekaku.api.spring.enumtype.AiRole;
import com.bekaku.api.spring.model.AiChatMessage;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AiChatMessageService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseChatMemory implements ChatMemory {

    private final AiChatMessageService aiChatMessageService;
    private final AppProperties appProperties;

    public DatabaseChatMemory(AiChatMessageService aiChatMessageService, AppProperties appProperties) {
        this.aiChatMessageService = aiChatMessageService;
        this.appProperties = appProperties;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        // การบันทึกลง Database ของคุณทำอยู่ใน streamAnswer อยู่แล้ว
        // ดังนั้นปล่อยว่างไว้ (หรือจะย้ายตรรกะการ save มาไว้ที่นี่ในอนาคตก็ได้)
    }

    @Override
    public List<Message> get(String conversationId) {
        List<AiChatMessage> history = aiChatMessageService.findLastNMessagesByChatId(Long.valueOf(conversationId), appProperties.rag().memorySize());
        // Filter out the last (most recent) request that belongs to the USER.
        // Because that's the current question we're about to send to the via the .user(request.getMessage()) command.
        if (!history.isEmpty()) {
            AiChatMessage lastMsg = history.getLast();
            if (lastMsg.getAiRole() == AiRole.user) {
                history.removeLast();
            }
        }
        return history.stream().map(msg -> {
            return switch (msg.getAiRole()) {
                case user -> new UserMessage(msg.getContent());
                case system -> new SystemMessage(msg.getContent());
                default -> new AssistantMessage(msg.getContent());
            };
        }).collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        // (Optional) ลบประวัติ
    }
}
