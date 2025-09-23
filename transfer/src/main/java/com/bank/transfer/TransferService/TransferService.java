package com.bank.transfer.TransferService;

import com.bank.transfer.dto.AccountBalanceDto;
import com.bank.transfer.dto.TransferRequest;
import com.bank.transfer.dto.UserAccountDto;
import com.bank.transfer.exception.TransferOperationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    private final AccountClient accountClient;
    private final ExchangeClient exchangeClient;

    public TransferService(AccountClient accountClient, ExchangeClient exchangeClient) {
        this.accountClient = accountClient;
        this.exchangeClient = exchangeClient;
    }

    public void processTransferOperation(String login, @Valid TransferRequest request) {
        var account = accountClient.getUserAccount(login);
        final var fromBalance = getBalance(account, request.fromCurrency());
        final var toBalance = getBalance(account, request.toCurrency());

        if (fromBalance.balance() < request.value()) {
            throw new TransferOperationException("Недостаточно средсв на счеты " + fromBalance.currency());
        }
        double amountInFromCurrency = -1.0;
        if (!fromBalance.currency().equals(toBalance.currency())) {
            amountInFromCurrency = exchangeClient.convert(request);
        }
        accountClient.updateBalance(login, request.fromCurrency(), toBalance.balance() - amountInFromCurrency);
        accountClient.updateBalance(request.toLogin(), request.toCurrency(), toBalance.balance() + amountInFromCurrency);


    }

    private AccountBalanceDto getBalance(UserAccountDto account, String fromCurrency) {
        return account.balances().stream()
                .filter(b -> b.currency().equals(fromCurrency))
                .findFirst()
                .orElseThrow(() -> new TransferOperationException("Счёт в валюте " + fromCurrency + " не существует"));
    }
}
