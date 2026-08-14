package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.enumtype.AiDocumentType;
import com.bekaku.api.spring.exception.DocumentIngestionException;
import com.bekaku.api.spring.extraction.DocumentExtractor;
import com.bekaku.api.spring.extraction.DocumentExtractorFactory;
import com.bekaku.api.spring.model.AiDocumentMeta;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FileMime;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.properties.RagProperties;
import com.bekaku.api.spring.repository.AiDocumentMetaRepository;
import com.bekaku.api.spring.service.DocumentIngestionService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionServiceImpl implements DocumentIngestionService {

    private final DocumentExtractorFactory extractorFactory;
    private final VectorStore vectorStore;
    private final AiDocumentMetaRepository documentMetaRepository;
    private final AppProperties appProperties;

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
            vectorStore.add(chunks);
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

    private void safeRollbackVectors(List<String> vectorIds) {
        try {
            vectorStore.delete(vectorIds);
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
                vectorStore.delete(doc.getVectorIds()); // Delete from Qdrant
            }
            documentMetaRepository.delete(doc);
        }
    }

    @Override
    public void deleteDocument(AiDocumentMeta doc) {
        if (doc.getVectorIds() != null && !doc.getVectorIds().isEmpty()) {
            vectorStore.delete(doc.getVectorIds()); // Delete from Qdrant
        }
        documentMetaRepository.delete(doc);
    }
}
