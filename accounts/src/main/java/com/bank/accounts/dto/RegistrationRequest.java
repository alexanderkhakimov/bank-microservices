package com.bank.accounts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record RegistrationRequest(
        @NotBlank String keycloakId,
        @NotBlank String login,
        @NotBlank String password,
        @NotBlank String name,
        @Email String email,
        @NotNull @Past LocalDate birthdate
) {
}
