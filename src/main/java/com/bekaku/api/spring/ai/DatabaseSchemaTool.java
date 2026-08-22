package com.bekaku.api.spring.ai;


import com.bekaku.api.spring.dto.ChatSourceReference;
import com.bekaku.api.spring.dto.DatabaseSchemaResult;
import com.bekaku.api.spring.enumtype.AiChatSourceType;
import com.bekaku.api.spring.properties.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DatabaseSchemaTool {

    private final VectorStore schemaVectorStore;
    private final AppProperties appProperties;

    public DatabaseSchemaTool(
            @Qualifier("schemaVectorStore") Optional<VectorStore> schemaVectorStore,
            AppProperties appProperties
    ) {
        this.schemaVectorStore = schemaVectorStore.orElse(null);
        this.appProperties = appProperties;
    }

    @Tool(description = """
            Describe the database schema required to answer the user's question.
            
            Focus on database structure only:
            
            - relevant table or business entity
            - relevant column or field
            - relationships between tables
            - columns needed for filtering
            - columns needed for sorting or aggregation
            
            Do NOT write SQL.
            Do NOT provide actual database values.
            Do NOT include the user's complete question unless necessary.
            
            Example:
            
            User question:
            "รายการ permission code ทั้งหมดที่ขึ้นต้นด้วย api"
            
            Search query:
            "permission table, permission code column"
            
            User question:
            "ยอดขายเดือนนี้"
            
            Search query:
            "sales order table, sales amount column, order date column"
            """)
    public List<DatabaseSchemaResult> searchSchema(
            @ToolParam(description = """
                    Describe what database information is needed.
                    Include the business information requested by the user,
                    such as customer, order, sales, product, date range,
                    relationships, aggregation, etc.
                    """)
            String query,
            ToolContext toolContext
    ) {
        if (!appProperties.rag().databaseTools().enabled() || schemaVectorStore == null) {
            return Collections.emptyList();
        }
        log.info("AI called searchSchema Query: [{}]", query);
        List<Document> documents = retrieveSchemaContext(query);
        log.info("Schema search returned {} documents", documents.size());
//        for (int i = 0; i < documents.size(); i++) {
//            Document doc = documents.get(i);
//
//            log.info(
//                    "Schema[{}] score={}, id={}, metadata={}, text={}",
//                    i,
//                    doc.getScore(),
//                    doc.getId(),
//                    doc.getMetadata(),
//                    doc.getText()
//            );
//        }

        AiChatToolContext chatContext =
                getChatContext(toolContext);
        documents.forEach(doc -> {
            ChatSourceReference source =
                    ChatSourceReference.builder()
                            .type(AiChatSourceType.DATABASE_TABLE)
                            .schema(String.valueOf(
                                    doc.getMetadata()
                                            .getOrDefault("schema_name", "public")
                            ))
                            .tableName(String.valueOf(
                                    doc.getMetadata()
                                            .getOrDefault("table_name", "unknown")
                            ))
                            .score(doc.getScore())
                            .build();

            chatContext.addSource(source);
        });
        return documents.stream()
                .map(this::toResult)
                .toList();
    }

    private AiChatToolContext getChatContext(
            ToolContext toolContext) {

        Object context =
                toolContext.getContext()
                        .get("chatToolContext");

        if (!(context instanceof AiChatToolContext)) {
            throw new IllegalStateException(
                    "chatToolContext is missing"
            );
        }

        return (AiChatToolContext) context;
    }

    private List<Document> retrieveSchemaContext(String query) {
        try {
            SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .topK(appProperties.rag().topK())
                    .similarityThreshold(appProperties.rag().similarityThreshold())
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
