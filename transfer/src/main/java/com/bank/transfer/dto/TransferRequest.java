package com.bank.transfer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "Выберите валюту списания")
        @JsonProperty("from_currency")
        String fromCurrency,

        @NotBlank(message = "Выберите валюту зачисления")
        @JsonProperty("to_currency")
        String toCurrency,

        @Min(value = 1, message = "Должно быть больше 1")
        @NotNull(message = "Не должно быть пустым")
        BigDecimal amount,

        @NotBlank(message = "Пользователь обязателен")
        @JsonProperty("to_login")
        String toLogin
) {}