package com.bank.frontui.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserRegistrationRequest(
        @NotBlank String login,
        @NotBlank String password,
        @NotBlank String confirmPassword,
        @NotBlank String name,
        @Email String email,
        @NotNull @Past LocalDate birthdate
) {
}
