package com.bank.frontui.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountBalanceDto(
        Currency currency,
        @PositiveOrZero
        BigDecimal balance,
        @JsonProperty("isExists")
        boolean isExists
) {
}
