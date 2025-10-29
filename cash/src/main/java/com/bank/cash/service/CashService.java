package com.bank.cash.service;

import com.bank.cash.dto.AccountBalanceDto;
import com.bank.cash.dto.CashRequest;
import com.bank.cash.exception.CashOperationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class CashService {

    private final AccountClient accountClient;

    public void processCashOperation(String login, @Valid CashRequest cashRequest) {
        var userAccount = accountClient.getUserAccount(login);
        var balance = userAccount.balances().stream()
                .filter(b -> b.currency().equals(cashRequest.currency()))
                .findFirst()
                .orElseThrow(() -> new CashOperationException("Счёт в валюте " + cashRequest.currency() + " не существует"));

        final var newBalance = calculateBalance(cashRequest, balance);
        accountClient.updateBalance(login, cashRequest.currency(), newBalance);
    }

    private BigDecimal calculateBalance(CashRequest cashRequest, AccountBalanceDto balance) {
        return switch (cashRequest.action()) {
            case DEPOSIT -> balance.balance().add(cashRequest.value());
            case WITHDRAW -> {
                if (balance.balance().compareTo(cashRequest.value()) < 0) {
                    throw new CashOperationException("Недостаточно средств на счёте в валюте " + cashRequest.currency());
                }
                yield balance.balance().subtract(cashRequest.value());
            }
        };
    }
}
