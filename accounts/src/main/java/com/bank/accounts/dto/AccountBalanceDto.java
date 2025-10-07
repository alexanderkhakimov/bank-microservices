package com.bank.accounts.dto;

import com.bank.accounts.model.Currency;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceDto(
        UserAccountDto userAccountDto,

        Currency currency,
        @PositiveOrZero
        BigDecimal balance,

        boolean isExists
) {
}