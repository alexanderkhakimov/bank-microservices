package com.bank.transfer.TransferService;

import com.bank.transfer.dto.AccountBalanceUpdateRequest;
import com.bank.transfer.dto.UserAccountDto;
import com.bank.transfer.exception.TransferOperationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AccountClient {
    private final RestTemplate restTemplate;
    private final String accountServiceUrl = "http://localhost:8082/api/v1/accounts";

    public AccountClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserAccountDto getUserAccount(String login) {
        try {
            var userAccount = restTemplate.getForObject(accountServiceUrl + "/{login}", UserAccountDto.class, login);
            if (userAccount == null || userAccount.balances() == null) {
                throw new TransferOperationException("Пользователь или его счета не найдены: " + login);
            }
            return userAccount;
        } catch (Exception e) {
            throw new TransferOperationException("Не удалось получить данные счёта для пользователя: " + login, e);
        }
    }

    public void updateBalance(String login, String currency, double newBalance) {
        try {
            var balanceUpdateRequest = AccountBalanceUpdateRequest.builder()
                    .balance(newBalance)
                    .currency(currency)
                    .build();
            restTemplate.put(accountServiceUrl + "/{login}/updateBalance", balanceUpdateRequest, login);
        } catch (Exception e) {
            throw new TransferOperationException("Не удалось обновить баланс для пользователя " + login, e);
        }
    }
}
