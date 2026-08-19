package com.bekaku.api.spring.serviceImpl;

import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.AiChatMessageDto;
import com.bekaku.api.spring.mapper.AiChatMessageMapper;
import com.bekaku.api.spring.model.AiChatMessage;
import com.bekaku.api.spring.repository.AiChatMessageRepository;
import com.bekaku.api.spring.service.AiChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AiChatMessageServiceImpl implements AiChatMessageService {
    private final AiChatMessageRepository aiChatMessageRepository;
    private final AiChatMessageMapper modelMapper;

    @Override
    public ResponseListDto<AiChatMessageDto> findAllWithPaging(Pageable pageable) {
        Page<AiChatMessage> result = aiChatMessageRepository.findAll(pageable);
        return getListFromResult(result);
    }

    @Override
    public ResponseListDto<AiChatMessageDto> findAllWithSearch(SearchSpecification<AiChatMessage> specification, Pageable pageable) {
        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));
    }

    @Override
    public ResponseListDto<AiChatMessageDto> findAllBy(Specification<AiChatMessage> specification, Pageable pageable) {
        return getListFromResult(findAllPageSpecificationBy(specification, pageable));
    }

    @Override
    public Page<AiChatMessage> findAllPageSpecificationBy(Specification<AiChatMessage> specification, Pageable pageable) {
        return aiChatMessageRepository.findAll(specification, pageable);
    }

    @Override
    public Page<AiChatMessage> findAllPageSearchSpecificationBy(SearchSpecification<AiChatMessage> specification, Pageable pageable) {
        return aiChatMessageRepository.findAll(specification, pageable);
    }

    private ResponseListDto<AiChatMessageDto> getListFromResult(Page<AiChatMessage> result) {
        return new ResponseListDto<>(result.getContent()
                .stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList())
                , result.getTotalPages(), result.getTotalElements(), result.isLast());
    }

    @Override
    public List<AiChatMessage> findAll() {
        return aiChatMessageRepository.findAll();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AiChatMessage save(AiChatMessage aiChatMessage) {
        return aiChatMessageRepository.save(aiChatMessage);
    }

    @Transactional
    @Override
    public AiChatMessage update(AiChatMessage aiChatMessage) {
        return aiChatMessageRepository.save(aiChatMessage);
    }

    @Override
    public Optional<AiChatMessage> findById(Long id) {
        return aiChatMessageRepository.findById(id);
    }

    @Transactional
    @Override
    public void delete(AiChatMessage aiChatMessage) {
        aiChatMessageRepository.delete(aiChatMessage);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        aiChatMessageRepository.deleteById(id);
    }

    @Override
    public AiChatMessageDto convertEntityToDto(AiChatMessage aiChatMessage) {
        return modelMapper.toDto(aiChatMessage);
    }

    @Override
    public AiChatMessage convertDtoToEntity(AiChatMessageDto aiChatMessageDto) {
        return modelMapper.toEntity(aiChatMessageDto);
    }

    @Override
    public List<AiChatMessage> findLastNMessagesByChatId(Long chatId, int limit) {
        List<AiChatMessage> latestMessages = findByAiChatIdOrderByCreatedDateDesc(
                chatId,
                PageRequest.of(0, limit)
        );
        List<AiChatMessage> modifiableList = new ArrayList<>(latestMessages);
        Collections.reverse(modifiableList);
        return modifiableList;
    }

    @Override
    public List<AiChatMessage> findByAiChatIdOrderByCreatedDateDesc(Long chatId, Pageable pageable) {
        return aiChatMessageRepository.findByAiChatIdOrderByCreatedDateDesc(chatId, pageable);
    }

    @Transactional
    @Override
    public void deleteByAiChatId(Long chatId) {
        aiChatMessageRepository.deleteByAiChatId(chatId);
    }
}
