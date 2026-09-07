package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.ai.DatabaseSchemaTool;
import com.bekaku.api.spring.ai.PostgreSQLQueryTool;
import com.bekaku.api.spring.ai.UserActivityTool;
import com.bekaku.api.spring.dto.ChatRequest;
import com.bekaku.api.spring.exception.ChatStreamException;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AiChatMessageService;
import com.bekaku.api.spring.service.AiChatService;
import com.bekaku.api.spring.service.UnansweredPromptLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiRagChatServiceOwnershipTest {

    @Mock
    ChatClient.Builder chatClientBuilder;
    @Mock
    UnansweredPromptLogService unansweredPromptLogService;
    @Mock
    AppProperties appProperties;
    @Mock
    AiChatService aiChatService;
    @Mock
    AiChatMessageService aiChatMessageService;
    @Mock
    DatabaseSchemaTool databaseSchemaTool;
    @Mock
    PostgreSQLQueryTool postgreSQLQueryTool;
    @Mock
    UserActivityTool userActivityTool;
    @Mock
    ChatMemory chatMemory;

    @Test
    void rejectsResumingChatNotOwnedByAuthenticatedUserBeforeWriting() {
        long userId = 10L;
        long chatId = 20L;
        ChatRequest request = new ChatRequest();
        request.setMessage("private question");
        request.setConversationId(chatId);
        when(aiChatService.findByIdAndCreator(chatId, userId)).thenReturn(Optional.empty());

        AiRagChatServiceImpl service = new AiRagChatServiceImpl(
                Optional.empty(), chatClientBuilder, unansweredPromptLogService, appProperties,
                aiChatService, aiChatMessageService, databaseSchemaTool, postgreSQLQueryTool,
                userActivityTool, chatMemory);

        assertThatThrownBy(() -> service.streamAnswer(userId, request).collectList().block())
                .isInstanceOf(ChatStreamException.class)
                .hasMessageContaining("Chat not found");

        verify(aiChatService).findByIdAndCreator(chatId, userId);
        verifyNoInteractions(aiChatMessageService);
    }
}
