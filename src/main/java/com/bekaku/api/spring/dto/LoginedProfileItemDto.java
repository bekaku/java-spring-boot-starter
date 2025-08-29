package com.bekaku.api.spring.dto;

public record LoginedProfileItemDto(AppUserDto user, NotificationCount notificationCount) {
}
