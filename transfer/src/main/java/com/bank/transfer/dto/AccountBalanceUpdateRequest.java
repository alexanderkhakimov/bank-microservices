package com.bank.transfer.dto;

import lombok.Builder;

@Builder
public record AccountBalanceUpdateRequest(
        String currency,
        double balance
) {}
