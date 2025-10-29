package com.bank.cash.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceUpdateRequest(
        String currency,
        BigDecimal balance
) {}
