package com.bank.frontui.dto;

import com.bank.frontui.enums.CashAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CashFormRequest(
        @NotBlank(message = "Валюта обязательна") String currency,
        @NotNull(message = "Сумма обязательна") @Positive(message = "Сумма должна быть положительной") Double value,
        @NotBlank(message = "Действие обязательно") String action
) {
    public CashAction toCashAction() {
        return switch (action.toUpperCase()) {
            case "PUT" -> CashAction.DEPOSIT;
            case "GET" -> CashAction.WITHDRAW;
            default -> throw new IllegalArgumentException("Недопустимое действие: " + action);
        };
    }
}
