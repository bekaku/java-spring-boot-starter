package com.bekaku.api.spring.controller.ai;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.controller.api.BaseApiController;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.AiDocumentMetaDto;
import com.bekaku.api.spring.model.AiDocumentMeta;
import com.bekaku.api.spring.service.AiDocumentMetaService;
import com.bekaku.api.spring.util.ConstantData;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

import java.util.Optional;

@Slf4j
@RequestMapping(path = "/api/aiDocumentMeta")
@RestController
@RequiredArgsConstructor
public class AiDocumentMetaController extends BaseApiController {

    private final AiDocumentMetaService aiDocumentMetaService;
    private final I18n i18n;

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_list')")
    @GetMapping
    public ResponseListDto<AiDocumentMetaDto> findAll(Pageable pageable, @RequestParam(name = ConstantData.SEARCH_PARAMETER_ATT, required = false) String q) {
        SearchSpecification<AiDocumentMeta> specification = new SearchSpecification<>(getSearchCriteriaList(q));
        return aiDocumentMetaService.findAllWithSearch(specification, getPageable(pageable, AiDocumentMeta.getSort()));
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_manage')")
    @PostMapping
    public AiDocumentMetaDto create(@Valid @RequestBody AiDocumentMetaDto dto) {
        AiDocumentMeta aiDocumentMeta = aiDocumentMetaService.convertDtoToEntity(dto);
        aiDocumentMetaService.save(aiDocumentMeta);
        return aiDocumentMetaService.convertEntityToDto(aiDocumentMeta);
    }

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_manage')")
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

    @PreAuthorize("@permissionChecker.hasPermission('ai_document_meta_manage')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        Optional<AiDocumentMeta> aiDocumentMeta = aiDocumentMetaService.findById(id);
        if (aiDocumentMeta.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        aiDocumentMetaService.delete(aiDocumentMeta.get());
        return this.responseDeleteMessage();
    }
}
