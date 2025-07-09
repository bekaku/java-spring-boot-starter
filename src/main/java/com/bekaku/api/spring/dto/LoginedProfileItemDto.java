package com.bekaku.api.spring.dto;

public record LoginedProfileItemDto(UserDto user, NotificationCount notificationCount) {
}
