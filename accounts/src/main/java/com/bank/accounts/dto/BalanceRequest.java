package com.bank.accounts.dto;

import com.bank.accounts.model.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record BalanceRequest(
        @NotNull Currency currency,
        @PositiveOrZero double initialBalance
) {
}
