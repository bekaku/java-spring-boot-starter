package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.dto.AiChatDto;
import com.bekaku.api.spring.dto.AppUserDto;
import com.bekaku.api.spring.exception.ApiException;
import com.bekaku.api.spring.exception.BaseResponseException;
import com.bekaku.api.spring.model.AiChat;
import com.bekaku.api.spring.service.AiChatMessageService;
import com.bekaku.api.spring.service.AiChatService;
import com.bekaku.api.spring.service.AiRagChatService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatControllerOwnershipTest {

    private static final long USER_ID = 10L;
    private static final long CHAT_ID = 20L;

    @Mock
    AiChatService aiChatService;
    @Mock
    AiChatMessageService aiChatMessageService;
    @Mock
    AiRagChatService aiRagChatService;
    @Mock
    I18n i18n;
    @Mock
    HttpServletRequest request;

    private AiChatController controller;
    private AppUserDto authenticatedUser;

    @BeforeEach
    void setUp() throws Exception {
        controller = new AiChatController(aiChatService, aiChatMessageService, i18n, aiRagChatService);
        injectExceptionI18n(controller);

        authenticatedUser = new AppUserDto();
        authenticatedUser.setId(USER_ID);
    }

    @Test
    void rejectsMessageHistoryForChatNotOwnedByAuthenticatedUser() {
        when(i18n.getMessage("error.dataNotfound")).thenReturn("Data not found");
        when(aiChatService.findByIdAndCreator(CHAT_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.messages(
                authenticatedUser, CHAT_ID, request, Pageable.unpaged()))
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.getApiError().getStatus())
                                .isEqualTo(HttpStatus.NOT_FOUND));

        verifyNoInteractions(aiChatMessageService);
    }

    @Test
    void bindsNewChatToAuthenticatedUser() {
        AiChatDto requestDto = new AiChatDto().setTitle("New chat");
        AiChat chat = new AiChat();
        AiChatDto responseDto = new AiChatDto().setTitle("New chat");
        when(aiChatService.convertDtoToEntity(requestDto)).thenReturn(chat);
        when(aiChatService.convertEntityToDto(chat)).thenReturn(responseDto);

        AiChatDto result = controller.create(authenticatedUser, requestDto);

        assertThat(result).isSameAs(responseDto);
        assertThat(chat.getCreatedUser()).isEqualTo(USER_ID);
        assertThat(chat.getUpdatedUser()).isEqualTo(USER_ID);
        verify(aiChatService).save(chat);
    }

    private void injectExceptionI18n(BaseResponseException target) throws Exception {
        Field field = BaseResponseException.class.getDeclaredField("i18n");
        field.setAccessible(true);
        field.set(target, i18n);
    }
}
