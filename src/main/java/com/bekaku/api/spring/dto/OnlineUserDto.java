package com.bekaku.api.spring.dto;

import java.time.LocalDateTime;

public record OnlineUserDto(Long userId,
                            String email,
                            LocalDateTime lastActive) {
}
