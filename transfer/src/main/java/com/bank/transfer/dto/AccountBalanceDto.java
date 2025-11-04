package com.bank.transfer.dto;

import com.bank.transfer.enums.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceDto(
        Currency currency,
        BigDecimal balance,
        boolean isExists
) {}
