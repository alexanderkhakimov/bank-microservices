package com.bank.transfer.dto;

import lombok.Builder;

@Builder
public record AccountBalanceDto(
        Long id,
        String currency,
        double balance,
        boolean isExists
) {}
