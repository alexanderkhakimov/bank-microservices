package com.bank.frontui.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Выберите валюту списания")
        String fromCurrency,
        @NotBlank(message = "Выберите валюту зачисления")
        String toCurrency,
        @Min(value = 1, message = "Должно быть больше 1")
        @NotNull(message = "Не должно быть пустым")
        BigDecimal amount,
        @NotBlank(message = "Пользователь обязателен")
        String toLogin
) {
}