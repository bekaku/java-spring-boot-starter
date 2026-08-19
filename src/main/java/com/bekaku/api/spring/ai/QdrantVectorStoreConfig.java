package com.bekaku.api.spring.ai;


import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class QdrantVectorStoreConfig {

    @Value("${spring.ai.vectorstore.qdrant.host:localhost}")
    private String host;

    @Value("${spring.ai.vectorstore.qdrant.port:6334}")
    private int port;

    @Value("${spring.ai.vectorstore.qdrant.api-key:}")
    private String apiKey;

    @Value("${spring.ai.vectorstore.qdrant.use-tls:false}")
    private boolean useTls;

    private static final String DOCUMENT_COLLECTION_NAME = "rag_documents";
    private static final String SCHEMA_COLLECTION_NAME = "table_schemas";

    @Bean
    public QdrantClient qdrantClient() {
        var builder = QdrantGrpcClient.newBuilder(host, port, useTls);

        // Insert API Key if defined in yml
        if (apiKey != null && !apiKey.isBlank()) {
            builder.withApiKey(apiKey);
        }

        return new QdrantClient(builder.build());
    }

    /**
     * 1. VectorStore for the original document file (PDF, MD, Text)
     * Add @Primary so that the original code that doesn't specify @Qualifier will continue to run without crashing.
     */
    @Bean(name = "documentVectorStore")
    @Primary
    public VectorStore documentVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName(DOCUMENT_COLLECTION_NAME)
                .contentFieldName("doc_content")
                .initializeSchema(true)
                .build();
    }

    /**
     * 2. VectorStore is a new platform for storing table schemamas from PostgreSQL.
     */
    @Bean(name = "schemaVectorStore")
    public VectorStore schemaVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName(SCHEMA_COLLECTION_NAME)
                .contentFieldName("doc_content")
                .initializeSchema(true)
                .build();
    }
}
