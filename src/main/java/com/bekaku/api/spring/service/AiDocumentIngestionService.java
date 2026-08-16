package com.bekaku.api.spring.service;

import com.bekaku.api.spring.model.AiDocumentMeta;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.model.FileMime;

public interface AiDocumentIngestionService {
    AiDocumentMeta ingest(FileManager fileManager );
    AiDocumentMeta ingest(String mergedFilePath, String originalFileName, FileMime fileMime);
    void deleteDocument(String fileName);
    void deleteDocument(AiDocumentMeta aiDocumentMeta);
    void ingestDatabaseSchemas();
}
