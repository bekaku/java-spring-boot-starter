package com.bekaku.api.spring.extraction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Extractor for IMAGE and VIDEO files. Full OCR / speech-to-text is not wired up
 * here — instead this produces a single descriptive placeholder Document so the
 * file is still tracked, searchable by filename/metadata, and the pipeline stays
 * uniform. Replace the body of {@link #describe} with a real OCR (e.g. Tesseract)
 * or transcription (e.g. Whisper) call to enable true content search over media.
 */
@Slf4j
@Component
public class MediaPlaceholderDocumentExtractor implements DocumentExtractor {
    @Override
    public List<Document> extract(Path filePath, String originalFileName) {
        log.warn("No OCR/transcription engine configured — indexing placeholder text only for media file: {}",
                originalFileName);

        String placeholderText = describe(originalFileName);
        Document document = new Document(placeholderText, Map.of(
                "mediaFile", "true",
                "originalFileName", originalFileName
        ));
        return List.of(document);
    }

    private String describe(String originalFileName) {
        return "Media file uploaded: " + originalFileName
                + ". No text content was extracted automatically. "
                + "Configure an OCR or transcription engine to enable full-text search over this file.";
    }
}
