package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.ai.AiChatToolContext;
import com.bekaku.api.spring.ai.DatabaseSchemaTool;
import com.bekaku.api.spring.ai.PostgreSQLQueryTool;
import com.bekaku.api.spring.dto.ChatRequest;
import com.bekaku.api.spring.dto.ChatSourceReference;
import com.bekaku.api.spring.dto.ChatStreamEvent;
import com.bekaku.api.spring.enumtype.AiChatSourceType;
import com.bekaku.api.spring.enumtype.AiRole;
import com.bekaku.api.spring.exception.ChatStreamException;
import com.bekaku.api.spring.model.AiChat;
import com.bekaku.api.spring.model.AiChatMessage;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AiChatMessageService;
import com.bekaku.api.spring.service.AiChatService;
import com.bekaku.api.spring.service.AiDocumentMetaService;
import com.bekaku.api.spring.service.AiRagChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bekaku.api.spring.util.AppUtil.distinctByKey;


@Slf4j
@Service
public class AiRagChatServiceImpl implements AiRagChatService {
    private final ChatClient.Builder chatClientBuilder;
    private final AiDocumentMetaService aiDocumentMetaService;
    private final AppProperties appProperties;
    private static final String CONVERSATION_ID_KEY = "chat_memory_conversation_id";
    private final String TEST_COMPANY = "GATS";

    // 1. VectorStore สำหรับไฟล์เอกสารเดิม (rag_documents)
    private final VectorStore documentVectorStore;

    @Value("classpath:prompts/system-rag.txt")
    private Resource systemPromptResource;

    @Value("classpath:prompts/system-rag-db-tools.txt")
    private Resource systemPromptDbToolsResource;

    @Value("classpath:prompts/system-rag-generate-title.txt")
    private Resource generateTitlePromptResource;

    private final AiChatService aiChatService;
    private final AiChatMessageService aiChatMessageService;
    private final DatabaseSchemaTool databaseSchemaTool;
    private final PostgreSQLQueryTool postgreSQLQueryTool;

    private final MessageChatMemoryAdvisor chatMemoryAdvisor;

    public AiRagChatServiceImpl(
            @Qualifier("documentVectorStore") VectorStore documentVectorStore,
            ChatClient.Builder chatClientBuilder,
            AiDocumentMetaService aiDocumentMetaService,
            AppProperties appProperties,
            AiChatService aiChatService,
            AiChatMessageService aiChatMessageService,
            DatabaseSchemaTool databaseSchemaTool,
            PostgreSQLQueryTool postgreSQLQueryTool,
            ChatMemory chatMemory
    ) {
        this.documentVectorStore = documentVectorStore;
        this.chatClientBuilder = chatClientBuilder;
        this.aiDocumentMetaService = aiDocumentMetaService;
        this.appProperties = appProperties;
        this.aiChatService = aiChatService;
        this.aiChatMessageService = aiChatMessageService;
        this.databaseSchemaTool = databaseSchemaTool;
        this.postgreSQLQueryTool = postgreSQLQueryTool;
        this.chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    @Override
    public Flux<ChatStreamEvent> streamAnswer(Long userId, ChatRequest request) {
        return Mono.fromCallable(() -> {
                    boolean isNewChat = request.getConversationId() == null;

                    Long chatId;
                    if (isNewChat) {
                        AiChat chat = new AiChat();
                        chat.setTitle("New Chat");
                        chat.setCreatedUser(userId);
                        chat.setUpdatedUser(userId);
                        chat = aiChatService.save(chat);
                        chatId = chat.getId();
                    } else {
                        chatId = request.getConversationId();
                        if (!aiChatService.existsById(chatId)) {
                            throw new ChatStreamException("Chat not found with ID: " + chatId, null);
                        }
                    }

                    AiChat chatRef = new AiChat();
                    chatRef.setId(chatId);

                    AiChatMessage userMsg = new AiChatMessage();
                    userMsg.setAiChat(chatRef);
                    userMsg.setAiRole(AiRole.user);
                    userMsg.setContent(request.getMessage());
                    aiChatMessageService.save(userMsg);

                    aiChatService.updateLatestUpdateDate(chatId, LocalDateTime.now());

                    return chatId;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(finalChatId -> {
                    final String convIdStr = String.valueOf(finalChatId);
                    boolean isNewChat = request.getConversationId() == null;
                    Flux<ChatStreamEvent> idEventFlux = Flux.just(
                            ChatStreamEvent.builder().type("chat_id").content(convIdStr).build()
                    );

                    Flux<ChatStreamEvent> titleEventFlux = Flux.empty();
                    if (isNewChat) {
                        titleEventFlux = Mono.fromCallable(() -> generateTitle(request.getMessage()))
                                .map(title -> {
                                    aiChatService.updateTitle(finalChatId, title);
                                    log.info("Generated new chat title: {}", title);
                                    return ChatStreamEvent.builder().type("title").content(title).build();
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                                .flux();
                    }

                    // Retrieving RAG documents.
                    List<Document> retrievedDocs = retrieveContext(request.getMessage(), request.getFilterNames());
                    log.info("Found {} documents from Qdrant", retrievedDocs.size());

                    List<ChatSourceReference> documentSources =
                            toDocumentSourceReferences(retrievedDocs);

                    String documentContext = retrievedDocs.stream()
                            .map(Document::getText)
                            .collect(Collectors.joining("\n\n---\n\n"));

                    StringBuilder aiContentBuilder = new StringBuilder();
                    AiChatToolContext chatToolContext = new AiChatToolContext();

                    boolean databaseToolsEnabled = appProperties.rag()
                            .databaseTools()
                            .enabled();

                    Resource systemPrompt = !databaseToolsEnabled
                            ? systemPromptResource
                            : systemPromptDbToolsResource;

                    List<ToolCallback> toolCallbacks = new ArrayList<>();
                    if (databaseToolsEnabled) {
                        toolCallbacks.addAll(Arrays.asList(
                                ToolCallbacks.from(databaseSchemaTool)
                        ));

                        toolCallbacks.addAll(Arrays.asList(
                                ToolCallbacks.from(postgreSQLQueryTool)
                        ));
                    }

                    Flux<ChatStreamEvent> tokenStream = chatClientBuilder
                            .build()
                            .prompt()
                            //TODO
                            //mark this if you dont want to use system prompt
//                .system(s -> s.text(systemPrompt)
//                        .param("context", documentContext)
//                )
                            .user(request.getMessage())
                            .advisors(chatMemoryAdvisor)
                            .advisors(a -> a.param(
                                    ChatMemory.CONVERSATION_ID,
                                    convIdStr
                            ))
                            .tools(toolCallbacks)
                            .toolContext(Map.of("chatToolContext", chatToolContext))
                            .stream()
                            .chatResponse()
                            .flatMapIterable(response -> {
                                List<ChatStreamEvent> events = new ArrayList<>();

                                if (response.getResult() == null || response.getResult().getOutput() == null) {
                                    return events;
                                }

                                var output = response.getResult().getOutput();
                                var metadata = output.getMetadata();
                                String content = output.getText();

                                String thinking = null;
                                if (metadata.containsKey("reasoningContent")) {
                                    thinking = String.valueOf(metadata.get("reasoningContent"));
                                } else if (metadata.containsKey("reasoning_content")) {
                                    thinking = String.valueOf(metadata.get("reasoning_content"));
                                } else if (metadata.containsKey("reasoning")) {
                                    thinking = String.valueOf(metadata.get("reasoning"));
                                } else if (metadata.containsKey("thinking")) {
                                    thinking = String.valueOf(metadata.get("thinking"));
                                }

                                // 2. ส่ง Event Thinking ไป Frontend
                                if (thinking != null && !thinking.isBlank() && !"null".equalsIgnoreCase(thinking)) {
                                    events.add(ChatStreamEvent.builder()
                                            .type("thinking")
                                            .content(thinking)
                                            .build());
                                }

                                if (content != null && !content.isEmpty()) {
                                    events.add(ChatStreamEvent.builder().type("token").content(content).build());
                                    aiContentBuilder.append(content);
                                }

                                return events;
                            })
                            .onErrorResume(ex -> {
                                log.error("Streaming chat completion failed, conversationId={}", convIdStr, ex);
                                return Flux.just(ChatStreamEvent.builder()
                                        .type("error")
                                        .content("The assistant encountered an error while generating a response.")
                                        .build());
                            });

                    // Send the sources at the end.
                    Flux<ChatStreamEvent> sourcesEvent = Mono.fromSupplier(() -> {
                        List<ChatSourceReference> allSources =
                                new ArrayList<>(documentSources);
                        if (chatToolContext.getSources() != null) {
                            allSources.addAll(chatToolContext.getSources());
                        }

                        List<ChatSourceReference> uniqueSources = allSources.stream()
                                .filter(distinctByKey(source -> {
                                    if (source.getFileName() != null) {
                                        return source.getFileName();
                                    } else if (source.getTableName() != null) {
                                        return source.getSchema() + "." + source.getTableName();
                                    }
                                    return source.toString(); // Fallback
                                }))
                                .toList();

                        return ChatStreamEvent.builder()
                                .type("sources")
                                .content(serializeSources(uniqueSources))
                                .build();
                    }).flux();

                    // Create a process to save the AI text to the database after the stream ends.
                    Flux<ChatStreamEvent> saveAiMessageFlux = Mono.fromRunnable(() -> {

                                AiChat chatParent = new AiChat();
                                chatParent.setId(finalChatId);

                                AiChatMessage aiMsg = new AiChatMessage();
                                aiMsg.setAiChat(chatParent);
                                aiMsg.setAiRole(AiRole.assistant);
                                aiMsg.setContent(aiContentBuilder.toString());
                                aiChatMessageService.save(aiMsg);
                                log.info("Saved AI response to DB for chat ID: {}", finalChatId);
                            }).subscribeOn(Schedulers.boundedElastic())
                            .thenMany(Flux.empty()); // Use thenMany to avoid affecting the main stream.

                    // Send the "Done" message (ending) along with the room ID.
                    Flux<ChatStreamEvent> doneEvent = Flux.just(
                            ChatStreamEvent.builder().type("done").content(convIdStr).build());

                    // Streaming Order: ID -> Title -> Token -> Sources -> **Save AI DB** -> Done
                    return Flux.concat(idEventFlux, titleEventFlux, tokenStream, sourcesEvent, saveAiMessageFlux, doneEvent);
                });
    }

    private String generateTitle(String firstMessage) {
        try {
            String generatedTitle = chatClientBuilder.build().prompt()
                    .system(generateTitlePromptResource)
                    .user(firstMessage)
                    .call()
                    .content();


            return generatedTitle != null ? generatedTitle.trim().replace("\"", "") : "New Chat";
        } catch (Exception e) {
            log.error("Failed to generate title for new chat", e);
            return "New Chat";
        }
    }

    private List<Document> retrieveContext(String query, List<String> fileNames) {
        try {
            var searchRequestBuilder = SearchRequest.builder()
                    .query(query)
                    .topK(appProperties.rag().topK())
                    .similarityThreshold(appProperties.rag().similarityThreshold());

            FilterExpressionBuilder feb = new FilterExpressionBuilder();

            if (fileNames != null && !fileNames.isEmpty()) {
                searchRequestBuilder.filterExpression(
                        feb.in("fileName", fileNames.toArray()).build()
                );
            }

            return documentVectorStore.similaritySearch(searchRequestBuilder.build());
        } catch (Exception e) {
            throw new ChatStreamException("Failed to retrieve context from vector store", e);
        }
    }

    private List<ChatSourceReference> toDocumentSourceReferences(
            List<Document> documents) {

        return documents.stream()
                .map(doc -> ChatSourceReference.builder()
                        .type(AiChatSourceType.DOCUMENT)
                        .fileName(String.valueOf(
                                doc.getMetadata().getOrDefault("fileName", "unknown")
                        ))
                        .documentType(String.valueOf(
                                doc.getMetadata().getOrDefault("documentType", "unknown")
                        ))
                        .score(doc.getScore())
                        .build())
                .distinct()
                .toList();
    }

    private String serializeSources(List<ChatSourceReference> sources) {
        if (sources == null || sources.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sources.size(); i++) {
            ChatSourceReference s = sources.get(i);
            if (s == null) continue;

            sb.append("{")
                    .append("\"type\":\"").append(s.getType() != null ? s.getType().name() : "null").append("\",");
            if (s.getType() == AiChatSourceType.DATABASE_TABLE) {
                //  Database
                sb.append("\"schema\":\"").append(escape(s.getSchema())).append("\",")
                        .append("\"tableName\":\"").append(escape(s.getTableName())).append("\",");
            } else if (s.getType() == AiChatSourceType.DATABASE_QUERY) {
                // Database Query
                sb.append("\"query\":\"").append(escape(s.getQuery())).append("\",");
            } else {
                //  (Document)
                sb.append("\"fileName\":\"").append(escape(s.getFileName())).append("\",")
                        .append("\"documentType\":\"").append(escape(s.getDocumentType())).append("\",");
            }

            //  Score
            sb.append("\"score\":").append(s.getScore() == null ? "null" : s.getScore())
                    .append("}");

            if (i < sources.size() - 1) {
                sb.append(",");
            }
        }
        return sb.append("]").toString();
    }

    private String escape(String val) {
        if (val == null) return "";
        return val.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /*
            // Filter ค้นหาเฉพาะไฟล์ที่ Active อยู่
        String joinedNames = activeFiles.stream().map(name -> "'" + name + "'").collect(Collectors.joining(", "));
        String filterExpression = "fileName in [" + joinedNames + "]";
        // 🌟 ถ้ามีการระบุแผนก (และไม่ได้เลือก "All") ให้เอามาต่อท้ายเงื่อนไขด้วย &&
//        if (department != null && !department.isEmpty() && !department.equalsIgnoreCase("All")) {
//            filterExpression += " && department == '" + department + "'";
//        }

//        filterExpression += " && docType == '" + AiDocumentType.WI.toString() + "'";
        filterExpression += " && company == '" + TEST_COMPANY + "'";

        log.info("filterExpression:{}", filterExpression);
     */

}
