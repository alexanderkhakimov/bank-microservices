package com.bank.notifications.dto;

import lombok.Builder;

@Builder
public record NotificataUserRequestDto(
        String email,
        String message
) {
}
