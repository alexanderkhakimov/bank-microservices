package com.bank.accounts.dto;

import lombok.Builder;

@Builder
public record NotificationsUserRequestDto(
        String email,
        String message
) {
}
