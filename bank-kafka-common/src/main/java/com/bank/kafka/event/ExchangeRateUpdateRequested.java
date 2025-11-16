package com.bank.kafka.event;

import com.bank.kafka.enums.Currency;

import java.math.BigDecimal;


public record ExchangeRateUpdateRequested(
        Currency currency,
        BigDecimal value
) {}
