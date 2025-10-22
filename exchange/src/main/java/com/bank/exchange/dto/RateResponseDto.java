package com.bank.exchange.dto;

import com.bank.exchange.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class RateResponseDto {
    private Currency currency;
    private BigDecimal value;
}
