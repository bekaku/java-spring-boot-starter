package com.bekaku.api.spring.util;


import com.bekaku.api.spring.dto.ChatSourceReference;
import com.bekaku.api.spring.dto.DatabaseSchemaResult;
import com.bekaku.api.spring.enumtype.AiChatSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DatabaseSchemaTool {

    private final VectorStore schemaVectorStore;
    private final AiChatToolContext chatToolContext;

    public DatabaseSchemaTool(@Qualifier("schemaVectorStore") VectorStore schemaVectorStore,
                              AiChatToolContext chatToolContext) {
        this.schemaVectorStore = schemaVectorStore;
        this.chatToolContext = chatToolContext;
    }

    @Tool(description = """
            Search database schema information.
            
            Use this tool when the user's question requires information
            about database tables, columns, relationships, or when SQL
            needs to be generated.
            
            Do not use this tool for general conversation or questions
            that can be answered without database information.
            """)
    public List<DatabaseSchemaResult> searchSchema(
            @ToolParam(description = "Describe the database information needed")
            String query) {
        log.info("AI called searchSchema: {}", query);

        List<Document> documents = retrieveSchemaContext(query);

        documents.forEach(doc -> {

            ChatSourceReference source =
                    ChatSourceReference.builder()
                            .type(AiChatSourceType.DATABASE_TABLE)
                            .schema(String.valueOf(
                                    doc.getMetadata()
                                            .getOrDefault("schema", "public")
                            ))
                            .tableName(String.valueOf(
                                    doc.getMetadata()
                                            .getOrDefault("tableName", "unknown")
                            ))
                            .score(doc.getScore())
                            .build();

            chatToolContext.addSource(source);
        });
        return documents.stream()
                .map(this::toResult)
                .toList();
    }

    private List<Document> retrieveSchemaContext(String query) {
        try {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(5)
                    .similarityThreshold(0.60)
                    .build();

            return schemaVectorStore.similaritySearch(request);

        } catch (Exception e) {
            log.error("Failed to retrieve database schema context", e);
            return List.of();
        }
    }

    private DatabaseSchemaResult toResult(Document document) {

        return DatabaseSchemaResult.builder()
                .schema(String.valueOf(
                        document.getMetadata()
                                .getOrDefault("schema", "public")
                ))
                .tableName(String.valueOf(
                        document.getMetadata()
                                .getOrDefault("tableName", "unknown")
                ))
                .definition(document.getText())
                .score(document.getScore())
                .build();
    }
}
