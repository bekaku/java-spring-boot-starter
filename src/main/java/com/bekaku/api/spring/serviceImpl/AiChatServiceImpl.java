package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.AiChatDto;
import com.bekaku.api.spring.mapper.AiChatMapper;
import com.bekaku.api.spring.model.AiChat;
import com.bekaku.api.spring.repository.AiChatRepository;
import com.bekaku.api.spring.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class AiChatServiceImpl implements AiChatService {
    private final AiChatRepository aiChatRepository;
    private final AiChatMapper modelMapper;

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AiChatDto> findAllWithPaging(Pageable pageable) {
        Page<AiChat> result = aiChatRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AiChatDto> findAllWithSearch(SearchSpecification<AiChat> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseListDto<AiChatDto> findAllBy(Specification<AiChat> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AiChat> findAllPageSpecificationBy(Specification<AiChat> specification, Pageable pageable) {
        return aiChatRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AiChat> findAllPageSearchSpecificationBy(SearchSpecification<AiChat> specification, Pageable pageable) {
        return aiChatRepository.findAll(specification, pageable);
    }
    private ResponseListDto<AiChatDto> getListFromResult(Page<AiChat> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AiChat> findAll() {
        return aiChatRepository.findAll();
    }


    public AiChat save(AiChat aiChat) {
        return aiChatRepository.save(aiChat);
    }

    @Override
    public AiChat update(AiChat aiChat) {
        return aiChatRepository.save(aiChat);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AiChat> findById(Long id) {
        return aiChatRepository.findById(id);
    }

    @Override
    public void delete(AiChat aiChat) {
        aiChatRepository.delete(aiChat);
    }

    @Override
    public void deleteById(Long id) {
        aiChatRepository.deleteById(id);
    }

    @Override
    public AiChatDto convertEntityToDto(AiChat aiChat) {
        return modelMapper.toDto(aiChat);
    }

    @Override
    public AiChat convertDtoToEntity(AiChatDto aiChatDto) {
        return modelMapper.toEntity(aiChatDto);
    }

    @Override
    public void updateLatestUpdateDate(Long chatId, LocalDateTime updatedDate) {
        aiChatRepository.updateLatestUpdateDate(chatId, updatedDate);
    }
}
