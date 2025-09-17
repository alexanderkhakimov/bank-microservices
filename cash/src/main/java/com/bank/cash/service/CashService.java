package com.bank.cash.service;

import com.bank.cash.dto.AccountBalanceDto;
import com.bank.cash.dto.CashRequest;
import com.bank.cash.exception.CashOperationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CashService {
    private static final Logger logger = LoggerFactory.getLogger(CashService.class);
    private final AccountsClient accountsClient;

    public CashService(AccountsClient accountsClient) {
        this.accountsClient = accountsClient;
    }

    public void processCashOperation(String login, @Valid CashRequest cashRequest) {
        var userAccount = accountsClient.getUserAccount(login);
        var balance = userAccount.balances().stream()
                .filter(b -> b.currency().equals(cashRequest.currency()))
                .findFirst()
                .orElseThrow(() -> new CashOperationException("Счёт в валюте " + cashRequest.currency() + " не существует"));

        var newBalance = calculateBalance(cashRequest, balance);
        accountsClient.updateBalance(login, cashRequest.currency(), newBalance);
    }

    private double calculateBalance(CashRequest cashRequest, AccountBalanceDto balance) {
        return switch (cashRequest.action()) {
            case DEPOSIT -> balance.balance() + cashRequest.value();
            case WITHDRAW -> {
                var result = balance.balance() - cashRequest.value();
                if (result < 0) {
                    throw new CashOperationException("Недостаточно средств на счёте в валюте " + cashRequest.currency());
                }
                yield result;
            }
            default -> throw new CashOperationException("Такой операции над счётом не существует!");
        };
    }
}
