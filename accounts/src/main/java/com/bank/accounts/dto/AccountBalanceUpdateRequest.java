package com.bank.accounts.dto;

import com.bank.accounts.model.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceUpdateRequest(
        Currency currency,
        BigDecimal balance
) {}
