package com.bekaku.api.spring;


import com.bekaku.api.spring.service.AppUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @MockitoBean
    private AppUserService appUserService;

//    @InjectMocks
//    private UserService userService;

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 1542L;
        when(appUserService.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> appUserService.findById(userId));
    }
}