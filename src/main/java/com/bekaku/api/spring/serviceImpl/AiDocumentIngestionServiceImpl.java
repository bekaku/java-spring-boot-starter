package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.enumtype.AiDocumentType;
import com.bekaku.api.spring.exception.DocumentIngestionException;
import com.bekaku.api.spring.extraction.DocumentExtractor;
import com.bekaku.api.spring.extraction.DocumentExtractorFactory;
import com.bekaku.api.spring.model.AiDocumentMeta;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FileMime;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.repository.AiDocumentMetaRepository;
import com.bekaku.api.spring.service.AiDocumentIngestionService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
public class AiDocumentIngestionServiceImpl implements AiDocumentIngestionService {

    private final DocumentExtractorFactory extractorFactory;
    private final AiDocumentMetaRepository documentMetaRepository;
    private final AppProperties appProperties;
    private final JdbcTemplate jdbcTemplate;
    // 1. VectorStore สำหรับไฟล์เอกสารเดิม (rag_documents)
    private final VectorStore documentVectorStore;
    // 2. VectorStore สำหรับ Table Schemas (table_schemas)
    private final VectorStore schemaVectorStore;
    private static final String SCHEMA_COLLECTION_NAME = "table_schemas";

    public AiDocumentIngestionServiceImpl(
            DocumentExtractorFactory extractorFactory,
            AiDocumentMetaRepository documentMetaRepository,
            AppProperties appProperties,
            JdbcTemplate jdbcTemplate,
            @Qualifier("documentVectorStore") VectorStore documentVectorStore,
            @Qualifier("schemaVectorStore") VectorStore schemaVectorStore
    ) {
        this.extractorFactory = extractorFactory;
        this.documentMetaRepository = documentMetaRepository;
        this.appProperties = appProperties;
        this.jdbcTemplate = jdbcTemplate;
        this.documentVectorStore = documentVectorStore;
        this.schemaVectorStore = schemaVectorStore;
    }

    @Override
    public AiDocumentMeta ingest(FileManager fileManager) {
        String filePath = FileUtil.getDirectoryForUpload(appProperties.getUploadPath(), fileManager.getFilePath(), false);
        return ingest(filePath, fileManager.getOriginalFileName(), fileManager.getFileMime());
    }

    @Override
    public AiDocumentMeta ingest(String mergedFilePath, String originalFileName, FileMime fileMime) {
        Path filePath = Path.of(mergedFilePath);
        if (!Files.exists(filePath) || AppUtil.isEmpty(fileMime)) {
            throw new DocumentIngestionException("Merged file not found on disk: " + mergedFilePath);
        }

        var existingDoc = documentMetaRepository.findByFileName(originalFileName);
        if (existingDoc.isPresent()) {
            deleteDocument(originalFileName);
        }

        AiDocumentType documentType = FileUtil.resolveAiDocumentTypeByMime(fileMime.getName());
//        AiDocumentType documentType = FileUtil.resolveAiDocumentType(originalFileName);
        DocumentExtractor extractor = extractorFactory.getExtractor(documentType);

        log.info("Starting ingestion for file={} type={}", originalFileName, documentType);

        List<Document> rawDocuments = extractor.extract(filePath, originalFileName);

        Map<String, String> customMetadata = buildMetadata(originalFileName, fileMime.getName());
        for (Document doc : rawDocuments) {
            doc.getMetadata().putAll(customMetadata);
        }

        List<Document> chunks = splitDocuments(rawDocuments);
        if (chunks.isEmpty()) {
            throw new DocumentIngestionException("Splitting produced zero chunks for file: " + originalFileName);
        }

        List<String> vectorIds = chunks.stream().map(Document::getId).toList();

        try {
            documentVectorStore.add(chunks);
            log.info("Stored {} vector chunks in Qdrant for file={}", chunks.size(), originalFileName);
        } catch (Exception e) {
            throw new DocumentIngestionException("Failed to store embeddings in vector store for file: " + originalFileName, e);
        }

        AiDocumentMeta meta;
        try {
            meta = buildMeta(originalFileName, vectorIds, customMetadata, fileMime);
            meta = documentMetaRepository.save(meta);
        } catch (Exception e) {
            // Keep Postgres and Qdrant in sync: if metadata persistence fails, undo the vector write.
            log.error("Metadata persistence failed after vectors were stored; rolling back {} vectors for file={}",
                    vectorIds.size(), originalFileName, e);
            safeRollbackVectors(vectorIds);
            throw new DocumentIngestionException("Failed to persist document metadata for file: " + originalFileName, e);
        }

        if (appProperties.rag().deleteSourceAfterIngest()) {
            deleteSourceFileQuietly(filePath);
        }

        log.info("Ingestion complete: documentId={} file={} chunks={}", meta.getId(), originalFileName, chunks.size());
        return meta;
    }

    private Map<String, String> buildMetadata(String fileName, String type) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("fileName", fileName);
        metadata.put("documentType", type);
        metadata.put("ingestedAt", LocalDateTime.now().toString());
        return metadata;
    }

    private List<Document> splitDocuments(List<Document> rawDocuments) {
        // Note: Spring AI's TokenTextSplitter chunks by token count but does not currently
        // support a configurable overlap window. chunkOverlap is retained in RagProperties
        // for use with a custom splitter if stricter recall/continuity across chunk
        // boundaries becomes a requirement later.
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(appProperties.rag().chunkSize()) // Maximum number of tokens per chunk (typical recommended size)
                .withMinChunkSizeChars(appProperties.rag().minChunkSizeChars())    // Minimum number of characters before starting to find sentence intersections.
                .withMinChunkLengthToEmbed(appProperties.rag().minChunkLengthToEmbed())   // Minimum length of token allowed to be converted to a vector.
                .withKeepSeparator(true)        // Specify that sentence breaks (e.g., \n or spaces) should be preserved.
                .build();
        try {
            return splitter.apply(rawDocuments);
        } catch (Exception e) {
            throw new DocumentIngestionException("Text splitting failed", e);
        }
    }

    /*
        private AiDocumentMeta buildMeta(String fileName, List<String> vectorIds, Map<String, String> metaData, FileMime fileMime) {

            // Remove metadata keys that are not needed in the database
            metaData.remove("documentType");
            metaData.remove("fileName");

            AiDocumentMeta meta = new AiDocumentMeta();
            meta.setFileName(fileName);
            meta.setActive(true);
            meta.setVectorIds(vectorIds);
            meta.setFileMime(fileMime);
            meta.setMetadata(metaData);
            return meta;
        }
    */
    private AiDocumentMeta buildMeta(String fileName, List<String> vectorIds, Map<String, String> metaData, FileMime fileMime) {
        Map<String, String> dbMetadata = new HashMap<>(metaData);
        dbMetadata.remove("documentType");
        dbMetadata.remove("fileName");

        AiDocumentMeta meta = new AiDocumentMeta();
        meta.setFileName(fileName);
        meta.setActive(true);
        meta.setVectorIds(vectorIds);
        meta.setFileMime(fileMime);
        meta.setMetadata(dbMetadata);
        return meta;
    }

    private void safeRollbackVectors(List<String> vectorIds) {
        try {
            documentVectorStore.delete(vectorIds);
        } catch (Exception rollbackEx) {
            // This is the one failure mode that needs a human: vectors are now orphaned in Qdrant.
            log.error("CRITICAL: failed to roll back orphaned vectors {} — manual Qdrant cleanup required",
                    vectorIds, rollbackEx);
        }
    }

    private void deleteSourceFileQuietly(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete source file after ingestion: {}", filePath, e);
        }
    }

    @Transactional
    public void deleteDocument(String fileName) {
        var existingDoc = documentMetaRepository.findByFileName(fileName);
        if (existingDoc.isPresent()) {
            AiDocumentMeta doc = existingDoc.get();
            if (doc.getVectorIds() != null && !doc.getVectorIds().isEmpty()) {
                documentVectorStore.delete(doc.getVectorIds()); // Delete from Qdrant
            }
            documentMetaRepository.delete(doc);
        }
    }

    @Override
    public void deleteDocument(AiDocumentMeta doc) {
        if (doc.getVectorIds() != null && !doc.getVectorIds().isEmpty()) {
            documentVectorStore.delete(doc.getVectorIds()); // Delete from Qdrant
        }
        documentMetaRepository.delete(doc);
    }

    @Override
    public synchronized void ingestDatabaseSchemas() {
        try {
            log.info("Fetching tables and comments metadata from PostgreSQL...");

            String sql = """
                    SELECT
                        t.table_schema,
                        t.table_name,
                    
                        COALESCE(
                            obj_description(c.oid, 'pg_class'),
                            ''
                        ) AS table_comment,
                    
                        COALESCE(
                            pk.primary_key_columns,
                            ''
                        ) AS primary_key_columns,
                    
                        COALESCE(
                            fk.foreign_key_columns,
                            ''
                        ) AS foreign_key_columns,
                    
                        string_agg(
                            cols.column_name
                            || ' (' || cols.data_type || ')'
                            || CASE
                                WHEN cols.is_nullable = 'NO'
                                THEN ' NOT NULL'
                                ELSE ''
                               END
                            || ': '
                            || COALESCE(
                                pg_catalog.col_description(
                                    c.oid,
                                    cols.ordinal_position
                                ),
                                ''
                               ),
                            E'\\n- '
                            ORDER BY cols.ordinal_position
                        ) AS columns_text
                    
                    FROM information_schema.tables t
                    
                    JOIN pg_catalog.pg_class c
                        ON c.relname = t.table_name
                    
                    JOIN pg_catalog.pg_namespace n
                        ON n.oid = c.relnamespace
                        AND n.nspname = t.table_schema
                    
                    JOIN information_schema.columns cols
                        ON cols.table_schema = t.table_schema
                        AND cols.table_name = t.table_name
                    
                    LEFT JOIN (
                        SELECT
                            tc.table_schema,
                            tc.table_name,
                            string_agg(kcu.column_name, ', ' ORDER BY kcu.ordinal_position)
                                AS primary_key_columns
                        FROM information_schema.table_constraints tc
                        JOIN information_schema.key_column_usage kcu
                            ON tc.constraint_name = kcu.constraint_name
                            AND tc.table_schema = kcu.table_schema
                            AND tc.table_name = kcu.table_name
                        WHERE tc.constraint_type = 'PRIMARY KEY'
                        GROUP BY tc.table_schema, tc.table_name
                    ) pk
                        ON pk.table_schema = t.table_schema
                        AND pk.table_name = t.table_name
                    
                    LEFT JOIN (
                        SELECT
                            tc.table_schema,
                            tc.table_name,
                            string_agg(
                                kcu.column_name
                                || ' -> '
                                || ccu.table_schema
                                || '.' 
                                || ccu.table_name
                                || '.'
                                || ccu.column_name,
                                E'\\n- '
                                ORDER BY kcu.ordinal_position
                            ) AS foreign_key_columns
                        FROM information_schema.table_constraints tc
                        JOIN information_schema.key_column_usage kcu
                            ON tc.constraint_name = kcu.constraint_name
                            AND tc.table_schema = kcu.table_schema
                            AND tc.table_name = kcu.table_name
                        JOIN information_schema.constraint_column_usage ccu
                            ON ccu.constraint_name = tc.constraint_name
                            AND ccu.table_schema = tc.table_schema
                        WHERE tc.constraint_type = 'FOREIGN KEY'
                        GROUP BY tc.table_schema, tc.table_name
                    ) fk
                        ON fk.table_schema = t.table_schema
                        AND fk.table_name = t.table_name
                    
                    WHERE t.table_schema = 'public'
                      AND t.table_type = 'BASE TABLE'
                    
                    GROUP BY
                        t.table_schema,
                        t.table_name,
                        c.oid,
                        pk.primary_key_columns,
                        fk.foreign_key_columns
                    
                    ORDER BY t.table_name
                    """;

            List<Document> documents = jdbcTemplate.query(sql, (rs, rowNum) -> {

                String schemaName = rs.getString("table_schema");
                String tableName = rs.getString("table_name");
                String tableComment = rs.getString("table_comment");
                String columnsText = rs.getString("columns_text");
                String primaryKey = rs.getString("primary_key_columns");
                String foreignKeys = rs.getString("foreign_key_columns");

                String content = """
                        Schema: %s
                        Table: %s
                        Description: %s
                        
                        Primary Key:
                        - %s
                        
                        Columns:
                        - %s
                        
                        Foreign Keys:
                        - %s
                        """.formatted(
                        schemaName,
                        tableName,
                        tableComment,
                        primaryKey,
                        columnsText,
                        foreignKeys
                );

                return Document.builder()
                        .id(UUID.nameUUIDFromBytes(("schema:" + schemaName + ":" + tableName).getBytes(StandardCharsets.UTF_8)).toString())
                        .text(content)
                        .metadata("schema_name", schemaName)
                        .metadata("table_name", tableName)
                        .metadata("type", "TABLE_SCHEMA")
                        .build();
            });

            if (documents.isEmpty()) {
                log.warn("No public tables found in PostgreSQL to index.");
                return;
            }

            // บันทึก/อัปเดตลง Collection table_schemas ใน Qdrant
            schemaVectorStore.accept(documents);
            log.info("Successfully synced {} table schemas to Qdrant collection [{}]", documents.size(), SCHEMA_COLLECTION_NAME);

        } catch (Exception e) {
            log.error("Failed to sync database schemas to Qdrant: {}", e.getMessage(), e);
        }
    }
}
