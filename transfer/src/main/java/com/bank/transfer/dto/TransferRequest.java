package com.bank.transfer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransferRequest(
        @NotBlank(message = "Выберите валюту списания")
        String fromCurrency,
        @NotBlank(message = "Выберите валюту зачисления")
        String toCurrency,
        @Min(value = 1, message = "Должно быть больше 1")
        @NotNull(message = "Не должно быть пустым")
        double value,
        @NotBlank(message = "Пользователь обязателен")
        String toLogin
) {
}
