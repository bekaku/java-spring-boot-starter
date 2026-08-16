package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AiDocumentMetaDto;
import com.bekaku.api.spring.dto.IngestionResponse;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.model.AiDocumentMeta;
import com.bekaku.api.spring.model.FileManager;
import com.bekaku.api.spring.properties.AppProperties;
import com.bekaku.api.spring.service.AiDocumentMetaService;
import com.bekaku.api.spring.service.AiDocumentIngestionService;
import com.bekaku.api.spring.service.FileManagerService;
import com.bekaku.api.spring.specification.SearchSpecification;
import com.bekaku.api.spring.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(path = "/api/aiDocumentMeta")
@RestController
@RequiredArgsConstructor
public class AiDocumentMetaController extends BaseApiController {

    private final AiDocumentMetaService aiDocumentMetaService;
    private final I18n i18n;
    private final FileManagerService fileManagerService;
    private final AiDocumentIngestionService ingestionService;
    private final AppProperties appProperties;

    /**
     * Triggers extraction, chunking, embedding, and persistence for a file that has
     * already been fully merged via the chunk upload API
     */
    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_add')")
    @PostMapping("/ingest/{fileManagerId}")
    public ResponseEntity<IngestionResponse> ingest(@PathVariable("fileManagerId") Long fileManagerId) {


        Optional<FileManager> f = fileManagerService.findById(fileManagerId);
        if (f.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        AiDocumentMeta meta = ingestionService.ingest(f.get());
        if (appProperties.rag().deleteSourceAfterIngest()) {
            fileManagerService.deleteFileBy(f.get(), true);
        }

        IngestionResponse response = IngestionResponse.builder()
                .id(meta.getId())
                .fileName(meta.getFileName())
                .fileMime(meta.getFileMime().getName())
                .chunkCount(meta.getVectorIds().size())
                .message("Document ingested successfully")
                .build();

        return this.responseEntity(response, HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_add')")
    @PostMapping("/ingestDatabaseSchemas")
    public ResponseEntity<?> ingestDatabaseSchemas() {

        ingestionService.ingestDatabaseSchemas();
        return this.responseEntity(HttpStatus.OK);
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_list')")
    @GetMapping
    public ResponseListDto<AiDocumentMetaDto> findAll(HttpServletRequest request, Pageable pageable) {
        SearchSpecification<AiDocumentMeta> specification = ControllerUtil.buildSpecification(request, List.of());
        return aiDocumentMetaService.findAllWithSearch(specification, getPageable(pageable, AiDocumentMeta.getSort()));
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_add')")
    @PostMapping
    public AiDocumentMetaDto create(@Valid @RequestBody AiDocumentMetaDto dto) {
        AiDocumentMeta aiDocumentMeta = aiDocumentMetaService.convertDtoToEntity(dto);
        aiDocumentMetaService.save(aiDocumentMeta);
        return aiDocumentMetaService.convertEntityToDto(aiDocumentMeta);
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_edit')")
    @PutMapping("/{id}")
    public AiDocumentMetaDto update(@PathVariable("id") Long id, @Valid @RequestBody AiDocumentMetaDto dto) {
        AiDocumentMeta aiDocumentMeta = aiDocumentMetaService.convertDtoToEntity(dto);
        Optional<AiDocumentMeta> oldData = aiDocumentMetaService.findById(id);
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        aiDocumentMetaService.update(aiDocumentMeta);
        return aiDocumentMetaService.convertEntityToDto(aiDocumentMeta);
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_view')")
    @GetMapping("/{id}")
    public AiDocumentMetaDto findOne(@PathVariable("id") Long id) {
        Optional<AiDocumentMeta> aiDocumentMeta = aiDocumentMetaService.findById(id);
        if (aiDocumentMeta.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return aiDocumentMetaService.convertEntityToDto(aiDocumentMeta.get());
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        Optional<AiDocumentMeta> aiDocumentMeta = aiDocumentMetaService.findById(id);
        if (aiDocumentMeta.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        ingestionService.deleteDocument(aiDocumentMeta.get());
        return this.responseDeleteMessage();
    }
}
