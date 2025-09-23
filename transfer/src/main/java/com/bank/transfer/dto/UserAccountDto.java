package com.bank.transfer.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record UserAccountDto(
        Long id,
        String login,
        String name,
        LocalDate birthday,
        List<AccountBalanceDto> balances
){}
