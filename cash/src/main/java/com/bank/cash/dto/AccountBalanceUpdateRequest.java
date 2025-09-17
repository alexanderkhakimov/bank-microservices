package com.bank.cash.dto;

import lombok.Builder;

@Builder
public record AccountBalanceUpdateRequest(
        String currency,
        double balance
) {}
