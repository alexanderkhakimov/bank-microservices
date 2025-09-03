package com.bank.exchange_generator.model;

import lombok.Builder;

@Builder
public record ExchangeRate(String title, String name, double value) {
}
