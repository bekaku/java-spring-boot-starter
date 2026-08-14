package com.bekaku.api.spring.extraction;

import org.springframework.ai.document.Document;

import java.nio.file.Path;
import java.util.List;


/**
 * Strategy for turning a source file on disk into one or more Spring AI Document objects
 * (raw, pre-chunking) ready for splitting and embedding.
 */
public interface DocumentExtractor {
    List<Document> extract(Path filePath, String originalFileName);
}
