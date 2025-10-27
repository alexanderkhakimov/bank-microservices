package com.bank.transfer.TransferService;

import com.bank.transfer.dto.AccountBalanceDto;
import com.bank.transfer.dto.TransferRequest;
import com.bank.transfer.dto.UserAccountDto;
import com.bank.transfer.exception.TransferOperationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final AccountClient accountClient;
    private final ExchangeClient exchangeClient;

    public TransferService(AccountClient accountClient, ExchangeClient exchangeClient) {
        this.accountClient = accountClient;
        this.exchangeClient = exchangeClient;
    }

    public void processTransferOperation(String login, @Valid TransferRequest request) {
        final var fromAccount = accountClient.getUserAccount(login);
        final var fromBalance = getBalance(fromAccount, request.fromCurrency());

        if (fromBalance.balance().compareTo(request.amount()) < 0) {
            throw new TransferOperationException("Недостаточно средсв на счеты " + fromBalance.currency());
        }

        final var toAccount = accountClient.getUserAccount(request.toLogin());
        final var toBalance = getBalance(toAccount, request.toCurrency());


        BigDecimal convertedAmount = request.amount();
        if (!fromBalance.currency().equals(toBalance.currency())) {
            convertedAmount = exchangeClient.convert(request.fromCurrency(), request.toCurrency(), request.amount());
        }
        try {
            accountClient.updateBalance(login, request.fromCurrency(), fromBalance.balance().subtract(convertedAmount));
            accountClient.updateBalance(request.toLogin(), request.toCurrency(), toBalance.balance().add(convertedAmount));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private AccountBalanceDto getBalance(UserAccountDto account, String fromCurrency) {
        return account.balances().stream()
                .filter(b -> b.currency().equals(fromCurrency))
                .findFirst()
                .orElseThrow(() -> new TransferOperationException("Счёт в валюте " + fromCurrency + " не существует"));
    }
}
