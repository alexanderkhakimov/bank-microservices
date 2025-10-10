package com.bank.frontui.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegisterUserRequestDto(
        String login,
        String password,
        String name,
        String email,
        LocalDate birthdate) {
}
