package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.enumtype.AiDocumentType;
import com.bekaku.api.spring.model.AiDocumentMeta;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AiDocumentMetaService;
import com.bekaku.api.spring.service.AiRagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AiRagServiceImpl implements AiRagService {
    private final VectorStore vectorStore;
    private final ChatClient.Builder chatClientBuilder;
    private final AiDocumentMetaService aiDocumentMetaService;
    private final AppProperties appProperties;

    private final String TEST_COMPANY= "GATS";

    @Transactional
    public void ingestPdf(Resource resource, String originalFileName) {
        // 1. จัดการข้อมูลเก่า (ถ้ามีไฟล์ชื่อซ้ำ ให้ลบของเก่าทิ้งให้คลีนก่อน)
        var existingDoc = aiDocumentMetaService.findByFileName(originalFileName);
        if (existingDoc.isPresent()) {
            deleteDocument(originalFileName);
        }

        // 2. อ่านไฟล์และหั่นข้อความ
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource);
        List<Document> documents = pdfReader.get();
        TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                .withChunkSize(appProperties.rag().getChunkSize())             // จำนวน Token สูงสุดต่อ 1 Chunk (ขนาดแนะนำทั่วไป)
                .withMinChunkSizeChars(appProperties.rag().getChunkSizeChar())     // จำนวนตัวอักษรขั้นต่ำก่อนที่จะเริ่มหาจุดตัดประโยค
                .withMinChunkLengthToEmbed(appProperties.rag().getChunkLengthEmbed())   // ความยาวขั้นต่ำของ Token ที่จะถูกยอมให้เอาไปแปลงเป็น Vector
                .withKeepSeparator(true)        // กำหนดให้เก็บตัวแบ่งประโยค (เช่น \n หรือช่องว่าง) ไว้ด้วย
                .build();
        List<Document> splitDocuments = textSplitter.apply(documents);

        // add Metadata
        splitDocuments.forEach(doc -> {
            // ข้อมูลพื้นฐาน
            doc.getMetadata().put("fileName", originalFileName);
            doc.getMetadata().put("company", TEST_COMPANY);
            doc.getMetadata().put("docType", AiDocumentType.WI.toString());
//            doc.getMetadata().put("year", 2026);
//            doc.getMetadata().put("isConfidential", true);
        });

        List<String> chunkIds = splitDocuments.stream().map(Document::getId).collect(Collectors.toList());
        AiDocumentMeta newDoc = new AiDocumentMeta();
        newDoc.setFileName(originalFileName);
        newDoc.setActive(true);
        newDoc.setVectorIds(chunkIds);
        newDoc.setDocumentType(AiDocumentType.WI);
        aiDocumentMetaService.save(newDoc);

        vectorStore.add(splitDocuments);
    }

    @Transactional
    public void deleteDocument(String fileName) {
        var existingDoc = aiDocumentMetaService.findByFileName(fileName);
        if (existingDoc.isPresent()) {
            AiDocumentMeta doc = existingDoc.get();
            if (doc.getVectorIds() != null && !doc.getVectorIds().isEmpty()) {
                vectorStore.delete(doc.getVectorIds()); // Delete from Qdrant
            }
            aiDocumentMetaService.delete(doc);
        }
    }

    public Flux<String> askStream(String question) {
        List<String> activeFiles = aiDocumentMetaService.findAllActiveFileNames();
        if (activeFiles.isEmpty()) {
            return Flux.just("ขออภัย ยังไม่มีเอกสารในระบบให้ค้นหาครับ");
        }

        // Filter ค้นหาเฉพาะไฟล์ที่ Active อยู่
        String joinedNames = activeFiles.stream().map(name -> "'" + name + "'").collect(Collectors.joining(", "));
        String filterExpression = "fileName in [" + joinedNames + "]";
        // 🌟 ถ้ามีการระบุแผนก (และไม่ได้เลือก "All") ให้เอามาต่อท้ายเงื่อนไขด้วย &&
//        if (department != null && !department.isEmpty() && !department.equalsIgnoreCase("All")) {
//            filterExpression += " && department == '" + department + "'";
//        }

        filterExpression += " && docType == '" + AiDocumentType.WI.toString() + "'";
        filterExpression += " && company == '" + TEST_COMPANY + "'";

        log.info("filterExpression:{}", filterExpression);
        SearchRequest searchRequest = SearchRequest.builder()
                .filterExpression(filterExpression)
                .topK(3)                   // ดึงข้อมูลที่ตรงที่สุดมาแค่ 3 ชิ้น (ค่า Default มักจะเป็น 4)
                .similarityThreshold(0.75) // กำหนดความคล้ายคลึงขั้นต่ำ (0.0 - 1.0)
                .build();

        return chatClientBuilder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(searchRequest)
                        .build())
                .build()
                .prompt()
                .user(question)
                .stream()
                .content();
    }
}
