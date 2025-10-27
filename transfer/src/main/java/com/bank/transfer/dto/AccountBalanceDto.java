package com.bank.transfer.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceDto(
        Long id,
        String currency,
        BigDecimal balance,
        boolean isExists
) {}
