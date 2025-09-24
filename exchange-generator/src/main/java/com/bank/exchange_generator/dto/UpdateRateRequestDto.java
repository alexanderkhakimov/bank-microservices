package com.bank.exchange_generator.dto;

import com.bank.exchange_generator.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class UpdateRateRequestDto {
    private Currency currency;
    private BigDecimal value;
}
