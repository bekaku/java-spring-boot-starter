package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.AiDocumentMetaDto;
import com.bekaku.api.spring.mapper.AiDocumentMetaMapper;
import com.bekaku.api.spring.model.AiDocumentMeta;
import com.bekaku.api.spring.repository.AiDocumentMetaRepository;
import com.bekaku.api.spring.service.AiDocumentMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AiDocumentMetaServiceImpl implements AiDocumentMetaService {
    private final AiDocumentMetaRepository aiDocumentMetaRepository;
    private final AiDocumentMetaMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AiDocumentMetaDto> findAllWithPaging(Pageable pageable) {
        Page<AiDocumentMeta> result = aiDocumentMetaRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AiDocumentMetaDto> findAllWithSearch(SearchSpecification<AiDocumentMeta> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AiDocumentMetaDto> findAllBy(Specification<AiDocumentMeta> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AiDocumentMeta> findAllPageSpecificationBy(Specification<AiDocumentMeta> specification, Pageable pageable) {
        return aiDocumentMetaRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AiDocumentMeta> findAllPageSearchSpecificationBy(SearchSpecification<AiDocumentMeta> specification, Pageable pageable) {
        return aiDocumentMetaRepository.findAll(specification, pageable);
    }

    private ResponseListDto<AiDocumentMetaDto> getListFromResult(Page<AiDocumentMeta> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AiDocumentMeta> findAll() {
        return aiDocumentMetaRepository.findAll();
    }


    public AiDocumentMeta save(AiDocumentMeta aiDocumentMeta) {
        return aiDocumentMetaRepository.save(aiDocumentMeta);
    }

    @Override
    public AiDocumentMeta update(AiDocumentMeta aiDocumentMeta) {
        return aiDocumentMetaRepository.save(aiDocumentMeta);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AiDocumentMeta> findById(Long id) {
        return aiDocumentMetaRepository.findById(id);
    }

    @Override
    public void delete(AiDocumentMeta aiDocumentMeta) {
        aiDocumentMetaRepository.delete(aiDocumentMeta);
    }

    @Override
    public void deleteById(Long id) {
        aiDocumentMetaRepository.deleteById(id);
    }

    @Override
    public AiDocumentMetaDto convertEntityToDto(AiDocumentMeta aiDocumentMeta) {
        return modelMapper.toDto(aiDocumentMeta);
    }

    @Override
    public AiDocumentMeta convertDtoToEntity(AiDocumentMetaDto aiDocumentMetaDto) {
        return modelMapper.toEntity(aiDocumentMetaDto);
    }

    @Override
    public Optional<AiDocumentMeta> findByFileName(String fileName) {
        return aiDocumentMetaRepository.findByFileName(fileName);
    }

    @Override
    public List<String> findAllActiveFileNames() {
        return aiDocumentMetaRepository.findAllActiveFileNames();
    }
}
