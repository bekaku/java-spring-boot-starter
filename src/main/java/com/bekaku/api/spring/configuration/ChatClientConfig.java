package com.bekaku.api.spring.configuration;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class ChatClientConfig {

    /**
     * In-memory, per-conversationId sliding window of chat history. Swap for a
     * JDBC/Redis-backed ChatMemory implementation if history needs to survive restarts.
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

    /**
     * Base ChatClient wired with the RAG retrieval advisor. The advisor pulls
     * relevant chunks from Qdrant and injects them into the prompt automatically
     * on every call — the calling code stays a plain "ask a question" call.
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel, VectorStore vectorStore, ChatMemory chatMemory) {
        var searchRequest = SearchRequest.builder()
                .topK(5)
                .similarityThreshold(0.5)
                .build();

        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        You are a helpful assistant that answers questions using ONLY the
                        provided context retrieved from the knowledge base.
                        If the context does not contain enough information to answer,
                        say so plainly instead of guessing or using outside knowledge.
                        Cite the source file name when it is available in the context metadata.
                        """)
                .defaultAdvisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(searchRequest)
                                .build()
                )
                .build();
    }
}
