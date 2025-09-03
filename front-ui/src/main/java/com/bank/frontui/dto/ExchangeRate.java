package com.bank.frontui.dto;

import lombok.Builder;

@Builder
public record ExchangeRate(String title, String name, double value) {
}
