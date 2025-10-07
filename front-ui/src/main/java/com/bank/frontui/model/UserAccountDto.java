package com.bank.frontui.model;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserAccountDto(
        String login,

        String name,

        String email,

        LocalDate birthdate,

        List<AccountBalanceDto> balances
) {
    public UserAccountDto {
        balances = balances != null ? List.copyOf(balances) : List.of();
    }
}
