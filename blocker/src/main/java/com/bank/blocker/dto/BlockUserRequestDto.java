package com.bank.blocker.dto;

import com.bank.blocker.enums.Currency;
import lombok.Builder;

@Builder
public record BlockUserRequestDto(
        String login,
        Currency currency,
        String operation
) {
}
