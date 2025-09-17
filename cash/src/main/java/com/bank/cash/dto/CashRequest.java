package com.bank.cash.dto;

import com.bank.cash.enums.CashAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CashRequest(
        @NotBlank(message = "Валюта обязательна") String currency,
        @NotNull(message = "Сумма обязательна") @Positive(message = "Сумма должна быть положительной") Double value,
        @NotNull(message = "Действие обязательно") CashAction action
) {}
