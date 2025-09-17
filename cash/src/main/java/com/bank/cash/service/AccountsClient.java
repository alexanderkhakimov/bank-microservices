package com.bank.cash.service;

import com.bank.cash.dto.AccountBalanceUpdateRequest;
import com.bank.cash.dto.UserAccountDto;
import com.bank.cash.exception.CashOperationException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AccountsClient {
    private final RestTemplate restTemplate;
    private final String accountServiceUrl = "http://localhost:8082/api/v1/accounts";

    public AccountsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserAccountDto getUserAccount(String login) {
        try {
            var userAccount = restTemplate.getForObject(accountServiceUrl + "/{login}", UserAccountDto.class, login);
            if (userAccount == null || userAccount.balances() == null) {
                throw new CashOperationException("Пользователь или его счета не найдены: " + login);
            }
            return userAccount;
        } catch (Exception e) {
            throw new CashOperationException("Не удалось получить данные счёта для пользователя: " + login, e);
        }
    }

    public void updateBalance(String login, String currency, double newBalance) {
        try {
            var balanceUpdateRequest = AccountBalanceUpdateRequest.builder()
                    .balance(newBalance)
                    .currency(currency)
                    .build();
            restTemplate.put(accountServiceUrl + "/{login}/updateBalance", balanceUpdateRequest,login);
        } catch (Exception e) {
            throw new CashOperationException("Не удалось обновить баланс для пользователя " + login, e);
        }
    }
}
