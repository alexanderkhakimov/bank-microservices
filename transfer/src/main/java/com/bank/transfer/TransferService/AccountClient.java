package com.bank.transfer.TransferService;

import com.bank.transfer.config.properties.ClientProperties;
import com.bank.transfer.dto.AccountBalanceUpdateRequest;
import com.bank.transfer.dto.UserAccountDto;
import com.bank.transfer.exception.TransferOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
public class AccountClient {
    private final RestClient restClient;

    public AccountClient(RestClient.Builder restClient, ClientProperties clientProperties) {
        this.restClient = restClient
                .baseUrl(clientProperties.getExchangeClient().getBaseurl())
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
                throw new TransferOperationException("Пользователь или его счета не найдены: " + login);
            }
            return userAccount;
        } catch (Exception e) {
            throw new TransferOperationException("Не удалось получить данные счёта для пользователя: " + login, e);
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
            throw new TransferOperationException("Не удалось обновить баланс для пользователя " + login, e);
        }
    }
}
