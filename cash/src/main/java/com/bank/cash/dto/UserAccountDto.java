package com.bank.cash.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
public record UserAccountDto(
        String login,
        String name,
        String email,
        LocalDate birthdate,
        List<AccountBalanceDto> balances
){}
