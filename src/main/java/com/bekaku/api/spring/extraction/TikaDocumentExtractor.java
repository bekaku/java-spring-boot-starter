package com.bekaku.api.spring.extraction;

import com.bekaku.api.spring.exception.DocumentIngestionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

/**
 * Default extractor for text-bearing formats: pdf, docx/doc, pptx/ppt/ppsx,
 * xlsx/xls/xlsm, txt/md/rst/log, json/csv/tsv/yaml/xml, html/htm.
 * Apache Tika auto-detects the concrete parser to use based on file content/type.
 */
@Slf4j
@Component
public class TikaDocumentExtractor implements DocumentExtractor {

    @Override
    public List<Document> extract(Path filePath, String originalFileName) {
        try {
            TikaDocumentReader reader = new TikaDocumentReader(new FileSystemResource(filePath));
            List<Document> documents = reader.get();

            if (documents.isEmpty() || documents.stream().allMatch(d -> d.getText() == null || d.getText().isBlank())) {
                throw new DocumentIngestionException(
                        "Tika extracted no readable text from file: " + originalFileName);
            }
            return documents;
        } catch (DocumentIngestionException e) {
            throw e;
        } catch (Exception e) {
            throw new DocumentIngestionException("Tika extraction failed for file: " + originalFileName, e);
        }
    }
}
