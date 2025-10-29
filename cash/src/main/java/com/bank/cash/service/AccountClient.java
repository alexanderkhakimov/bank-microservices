package com.bank.cash.service;

import com.bank.cash.config.properties.ClientProperties;
import com.bank.cash.dto.AccountBalanceUpdateRequest;
import com.bank.cash.dto.UserAccountDto;
import com.bank.cash.exception.CashOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Slf4j
@Component
public class AccountClient {
    private final RestClient restClient;

    public AccountClient(RestClient.Builder restClient, ClientProperties clientProperties) {
        this.restClient = restClient
                .baseUrl(clientProperties.getUserClient().getBaseurl())
                .build();
    }

    public UserAccountDto getUserAccount(String login) {
        log.info("Запрашиваем аккаунт пользователя {}", login);
        try {
            final var userAccount = restClient.get()
                    .uri("/{login}", login)
                    .retrieve()
                    .body(UserAccountDto.class);
            if (userAccount == null || userAccount.balances() == null) {
                throw new CashOperationException("Пользователь или его счета не найдены: " + login);
            }
            return userAccount;
        } catch (Exception e) {
            throw new CashOperationException("Не удалось получить данные счёта для пользователя: " + login, e);
        }
    }

    public void updateBalance(String login, String currency, BigDecimal newBalance) {
        log.info("Обновлчем счета {} аккаунта пользователя {}", currency, login);
        try {
            final var balanceUpdateRequest = AccountBalanceUpdateRequest.builder()
                    .balance(newBalance)
                    .currency(currency)
                    .build();
            restClient.put()
                    .uri("/{login}/updateBalance", login)
                    .body(balanceUpdateRequest)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new CashOperationException("Не удалось обновить баланс для пользователя " + login, e);
        }
    }
}

