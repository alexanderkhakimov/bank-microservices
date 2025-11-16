package com.bank.exchange.dto;

import com.bank.kafka.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRateRequestDto {
    private Currency currency;
    private BigDecimal value;
}
