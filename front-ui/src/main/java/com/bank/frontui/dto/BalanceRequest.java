package com.bank.frontui.dto;


import com.bank.frontui.model.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BalanceRequest(
        @NotNull Currency currency,
        @PositiveOrZero BigDecimal initialBalance
) {
}
