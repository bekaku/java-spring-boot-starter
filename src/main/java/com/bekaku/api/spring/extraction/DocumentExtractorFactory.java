package com.bekaku.api.spring.extraction;

import com.bekaku.api.spring.enumtype.AiDocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Selects the correct DocumentExtractor implementation based on the resolved AiDocumentType.
 */
@Component
@RequiredArgsConstructor
public class DocumentExtractorFactory {
    private final TikaDocumentExtractor tikaDocumentExtractor;
    private final MediaPlaceholderDocumentExtractor mediaPlaceholderDocumentExtractor;

    public DocumentExtractor getExtractor(AiDocumentType type) {
        return switch (type) {
            case IMAGE, VIDEO -> mediaPlaceholderDocumentExtractor;
            case DOCUMENT, SPREADSHEET, PRESENTATION, TEXT, STRUCTURED_DATA, WEB -> tikaDocumentExtractor;
        };
    }
}
