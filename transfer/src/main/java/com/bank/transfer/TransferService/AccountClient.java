package com.bank.transfer.TransferService;

import com.bank.transfer.config.properties.ClientProperties;
import com.bank.transfer.dto.AccountBalanceUpdateRequest;
import com.bank.transfer.dto.UserAccountDto;
import com.bank.transfer.exception.TransferOperationException;
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
                    .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                        log.error("Клиентская ошибка {} при запросе аккаунта пользователя {}",
                                response.getStatusCode(), login);
                        throw new TransferOperationException("Клиентская ошибка при запросе аккаунта: " + response.getStatusCode());
                    })
                    .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                        log.error("Серверная ошибка {} при запросе аккаунта пользователя {}",
                                response.getStatusCode(), login);
                        throw new TransferOperationException("Серверная ошибка при запросе аккаунта: " + response.getStatusCode());
                    })
                    .body(UserAccountDto.class);

            if (userAccount == null) {
                log.warn("Получен null ответ для пользователя {}", login);
                throw new TransferOperationException("Получен пустой ответ для пользователя: " + login);
            }

            if (userAccount.balances() == null || userAccount.balances().isEmpty()) {
                log.warn("У пользователя {} нет счетов или balances=null", login);
                throw new TransferOperationException("У пользователя нет счетов: " + login);
            }

            log.info("Успешно получен аккаунт пользователя {}: {}", login, userAccount);
            return userAccount;

        }catch (Exception e) {
            log.error("Неизвестная ошибка при запросе аккаунта пользователя {}: {}", login, e.getMessage(), e);
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
