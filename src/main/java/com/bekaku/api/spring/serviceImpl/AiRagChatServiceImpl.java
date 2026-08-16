package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ChatRequest;
import com.bekaku.api.spring.dto.ChatSourceReference;
import com.bekaku.api.spring.dto.ChatStreamEvent;
import com.bekaku.api.spring.enumtype.AiChatSourceType;
import com.bekaku.api.spring.enumtype.AiRole;
import com.bekaku.api.spring.exception.ChatStreamException;
import com.bekaku.api.spring.model.AiChat;
import com.bekaku.api.spring.model.AiChatMessage;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.repository.AiChatRepository;
import com.bekaku.api.spring.service.AiChatMessageService;
import com.bekaku.api.spring.service.AiChatService;
import com.bekaku.api.spring.service.AiDocumentMetaService;
import com.bekaku.api.spring.service.AiRagChatService;
import com.bekaku.api.spring.util.AiChatToolContext;
import com.bekaku.api.spring.util.UuidUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Transactional
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
    @Value("classpath:prompts/system-rag-generate-title.txt")
    private Resource generateTitlePromptResource;

    private final AiChatService aiChatService;
    private final AiChatMessageService aiChatMessageService;
    private final ToolCallbackProvider databaseSchemaToolProvider;
    private final AiChatToolContext chatToolContext;

    public AiRagChatServiceImpl(
            @Qualifier("documentVectorStore") VectorStore documentVectorStore,
            ChatClient.Builder chatClientBuilder,
            AiDocumentMetaService aiDocumentMetaService,
            AppProperties appProperties,
            AiChatService aiChatService,
            AiChatMessageService aiChatMessageService,
//            @Qualifier("postgres-db")
            ToolCallbackProvider databaseSchemaToolProvider,
            AiChatToolContext chatToolContext
    ) {
        this.documentVectorStore = documentVectorStore;
        this.chatClientBuilder = chatClientBuilder;
        this.aiDocumentMetaService = aiDocumentMetaService;
        this.appProperties = appProperties;
        this.aiChatService = aiChatService;
        this.aiChatMessageService = aiChatMessageService;
        this.databaseSchemaToolProvider = databaseSchemaToolProvider;
        this.chatToolContext = chatToolContext;
    }

    public Flux<ChatStreamEvent> streamAnswer(ChatRequest request) {
        boolean isNewChat = request.getConversationId() == null;

        AiChat chat;
        if (isNewChat) {
            chat = new AiChat();
            chat.setTitle("New Chat");
            chat = aiChatService.save(chat); // Save to get the ID
        } else {
            chat = aiChatService.findById(request.getConversationId())
                    .orElseThrow(() -> new ChatStreamException("Chat not found with ID: " + request.getConversationId(), null));
        }

        final Long finalChatId = chat.getId();
        final String convIdStr = String.valueOf(finalChatId);

        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setAiChat(chat);
        userMsg.setAiRole(AiRole.user);
        userMsg.setContent(request.getMessage());
        aiChatMessageService.save(userMsg);
        //update chat latest date for sorting recent chats
        aiChatService.updateLatestUpdateDate(chat.getId(), LocalDateTime.now());


        Flux<ChatStreamEvent> idEventFlux = Flux.just(
                ChatStreamEvent.builder().type("chat_id").content(convIdStr).build()
        );

        Flux<ChatStreamEvent> titleEventFlux = Flux.empty();
        if (isNewChat) {
            final AiChat currentChat = chat;
            titleEventFlux = Mono.fromCallable(() -> generateTitle(request.getMessage()))
                    .map(title -> {
                        currentChat.setTitle(title);
                        aiChatService.save(currentChat);
                        log.info("Generated new chat title: {}", title);
                        return ChatStreamEvent.builder().type("title").content(title).build();
                    })
                    .flux();
        }

        // Retrieving RAG documents.
        List<Document> retrievedDocs = retrieveContext(request.getMessage(), request.getFilterNames());
        log.info("Found {} documents from Qdrant", retrievedDocs.size());

//        List<Document> retrievedSchemas =
//                retrieveSchemaContext(request.getMessage());
//        log.info("Found {} database schemas", retrievedSchemas.size());

        List<ChatSourceReference> documentSources =
                toDocumentSourceReferences(retrievedDocs);
//        List<ChatSourceReference> sources = new ArrayList<>();
//        sources.addAll(toDocumentSourceReferences(retrievedDocs));
//        sources.addAll(toDatabaseSourceReferences(retrievedSchemas));

        String documentContext = retrievedDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

//        String schemaContext = retrievedSchemas.stream()
//                .map(Document::getText)
//                .collect(Collectors.joining("\n\n---\n\n"));


        StringBuilder aiContentBuilder = new StringBuilder();
        Flux<ChatStreamEvent> tokenStream = chatClientBuilder.build()
                .prompt()
                //mark this if you dont want to use system prompt
                .system(s -> s.text(systemPromptResource)
                                .param("context", documentContext)
//                        .param("schemaContext", schemaContext)
                )
                .user(request.getMessage())
                .tools(
                        databaseSchemaToolProvider
                )
                // .advisors(a -> a.param(CONVERSATION_ID_KEY, convIdStr)) // เปิดใช้ถ้าเชื่อม PersistentChatMemory แล้ว
                .stream()
                .chatResponse()
                .flatMapIterable(response -> {
                    List<ChatStreamEvent> events = new ArrayList<>();

                    if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
                        return events;
                    }

                    var output = response.getResult().getOutput();
                    String content = output.getText();

                    String thinking = null;
                    if (output.getMetadata().containsKey("thinking")) {
                        thinking = String.valueOf(output.getMetadata().get("thinking"));
                    } else if (output.getMetadata().containsKey("reasoning_content")) {
                        thinking = String.valueOf(output.getMetadata().get("reasoning_content"));
                    }

                    if (thinking != null && !thinking.isEmpty() && !"null".equals(thinking)) {
                        events.add(ChatStreamEvent.builder().type("thinking").content(thinking).build());
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
//        Flux<ChatStreamEvent> sourcesEvent = Flux.just(
//                ChatStreamEvent.builder().type("sources").content(serializeSources(documentSources)).build());

        Flux<ChatStreamEvent> sourcesEvent =
                tokenStream.thenMany(
                        Mono.fromSupplier(() -> {
                            List<ChatSourceReference> sources =
                                    new ArrayList<>(documentSources);
                            sources.addAll(
                                    chatToolContext.getSources()
                            );
                            return ChatStreamEvent.builder()
                                    .type("sources")
                                    .content(serializeSources(sources))
                                    .build();
                        })
                );

        // Create a process to save the AI text to the database after the stream ends.
        final AiChat aichat = chat;
        Flux<ChatStreamEvent> saveAiMessageFlux = Mono.fromRunnable(() -> {
            AiChatMessage aiMsg = new AiChatMessage();
            aiMsg.setAiChat(aichat);
            aiMsg.setAiRole(AiRole.assistant);

            //Gather the accumulated text. If you have any ideas, you could put them in metadata or a new column.
            aiMsg.setContent(aiContentBuilder.toString());

            // If you have fields in your database that accept metadata, you can store the thinking and sources in JSON format.
            // aiMsg.setMetadata("{ \"thinking\": \"...\", \"sources\": [...] }");

            aiChatMessageService.save(aiMsg);
            log.info("Saved AI response to DB for chat ID: {}", finalChatId);
        }).thenMany(Flux.empty()); // Use thenMany to avoid affecting the main stream.

        // Send the "Done" message (ending) along with the room ID.
        Flux<ChatStreamEvent> doneEvent = Flux.just(
                ChatStreamEvent.builder().type("done").content(convIdStr).build());

        // Streaming Order: ID -> Title -> Token -> Sources -> **Save AI DB** -> Done
        return Flux.concat(idEventFlux, titleEventFlux, tokenStream, sourcesEvent, saveAiMessageFlux, doneEvent);
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

    private List<ChatSourceReference> toDatabaseSourceReferences(
            List<Document> schemas) {

        return schemas.stream()
                .map(doc -> ChatSourceReference.builder()
                        .type(AiChatSourceType.DATABASE_TABLE)
                        .schema(String.valueOf(
                                doc.getMetadata().getOrDefault("schema", "public")
                        ))
                        .tableName(String.valueOf(
                                doc.getMetadata().getOrDefault("tableName", "unknown")
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
