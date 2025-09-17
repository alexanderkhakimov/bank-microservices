package com.bank.cash.dto;

import lombok.Builder;

@Builder
public record AccountBalanceDto(
        Long id,
        String currency,
        double balance,
        boolean isExists
) {}
