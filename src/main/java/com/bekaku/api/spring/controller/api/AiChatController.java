package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AiChatMessageDto;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.dto.ChatRequest;
import com.bekaku.api.spring.dto.ChatStreamEvent;
import com.bekaku.api.spring.dto.ResponseListDto;
import com.bekaku.api.spring.dto.AiChatDto;
import com.bekaku.api.spring.model.AiChat;
import com.bekaku.api.spring.model.AiChatMessage;
import com.bekaku.api.spring.service.AiChatMessageService;
import com.bekaku.api.spring.service.AiChatService;
import com.bekaku.api.spring.service.AiRagChatService;
import com.bekaku.api.spring.specification.SearchCriteria;
import com.bekaku.api.spring.specification.SearchOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.bekaku.api.spring.specification.SearchSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import com.bekaku.api.spring.util.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import com.bekaku.api.spring.util.ConstantData;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(path = "/api/aiChat")
@RestController
@RequiredArgsConstructor
public class AiChatController extends BaseApiController {

    private final AiChatService aiChatService;
    private final AiChatMessageService aiChatMessageService;
    private final I18n i18n;
    private final AiRagChatService aiRagChatService;

    /**
     * Streams a RAG-grounded answer as Server-Sent Events.
     * Event payloads are ChatStreamEvent JSON objects with type: token | sources | done | error.
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatStreamEvent> streamChat(@AuthenticationPrincipal AppUserDto auth, @Valid @RequestBody ChatRequest request) {
        return aiRagChatService.streamAnswer(auth.getId(), request);
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseListDto<AiChatDto>> findAll(@AuthenticationPrincipal AppUserDto auth, HttpServletRequest request, Pageable pageable) {
        SearchSpecification<AiChat> specification = ControllerUtil.buildSpecification(request, List.of());
        specification.add(new SearchCriteria("createdUser", auth.getId(), SearchOperation.EQUAL));
        return this.responseEntity(aiChatService.findAllWithSearch(specification, getPageable(pageable, AiChat.getSort())), HttpStatus.OK);
    }

    @GetMapping("/messages/{aiChatId}")
    public ResponseEntity<ResponseListDto<AiChatMessageDto>> messages(@PathVariable("aiChatId") Long aiChatId,
                                                                      HttpServletRequest request,
                                                                      Pageable pageable) {
        SearchSpecification<AiChatMessage> specification = ControllerUtil.buildSpecification(request, List.of());
        specification.add(new SearchCriteria("aiChat.id", aiChatId, SearchOperation.EQUAL));
        return this.responseEntity(aiChatMessageService.findAllWithSearch(specification, getPageable(pageable, AiChatMessage.getSort())), HttpStatus.OK);
    }

    @PostMapping
    public AiChatDto create(@Valid @RequestBody AiChatDto dto) {
        AiChat aiChat = aiChatService.convertDtoToEntity(dto);
        aiChatService.save(aiChat);
        return aiChatService.convertEntityToDto(aiChat);
    }

    @PutMapping("/{id}")
    public AiChatDto update(@PathVariable("id") Long id, @Valid @RequestBody AiChatDto dto) {
        AiChat aiChat = aiChatService.convertDtoToEntity(dto);
        Optional<AiChat> oldData = aiChatService.findById(id);
        if (oldData.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        aiChatService.update(aiChat);
        return aiChatService.convertEntityToDto(aiChat);
    }

    @GetMapping("/{id}")
    public AiChatDto findOne(@PathVariable("id") Long id) {
        Optional<AiChat> aiChat = aiChatService.findById(id);
        if (aiChat.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        return aiChatService.convertEntityToDto(aiChat.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        Optional<AiChat> aiChat = aiChatService.findById(id);
        if (aiChat.isEmpty()) {
            throw this.responseErrorNotfound();
        }
        aiChatService.delete(aiChat.get());
        return this.responseDeleteMessage();
    }
}
