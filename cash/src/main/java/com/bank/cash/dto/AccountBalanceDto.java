package com.bank.cash.dto;

import com.bank.cash.enums.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceDto(
        Currency currency,
        BigDecimal balance,
        boolean isExists
) {}
