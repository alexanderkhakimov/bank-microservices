package com.bank.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateRequest(
        @NotNull String name,
        @NotNull String login,
        @NotNull @Past LocalDate birthdate
) {
}
