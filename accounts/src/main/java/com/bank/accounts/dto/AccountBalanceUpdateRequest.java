package com.bank.accounts.dto;

import lombok.Builder;

@Builder
public record AccountBalanceUpdateRequest(
        String currency,
        double balance
) {}
