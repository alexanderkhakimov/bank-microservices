package com.bank.notifications.dto;

import lombok.Builder;

@Builder
public record NotificationsUserRequestDto(
        String email,
        String message
) {
}
