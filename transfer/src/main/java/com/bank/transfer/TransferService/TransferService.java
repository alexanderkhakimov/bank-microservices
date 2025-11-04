package com.bank.transfer.TransferService;

import com.bank.transfer.dto.AccountBalanceDto;
import com.bank.transfer.dto.TransferRequest;
import com.bank.transfer.dto.UserAccountDto;
import com.bank.transfer.exception.TransferOperationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransferService {
    private final AccountClient accountClient;
    private final ExchangeClient exchangeClient;

    public TransferService(AccountClient accountClient, ExchangeClient exchangeClient) {
        this.accountClient = accountClient;
        this.exchangeClient = exchangeClient;
    }

    public void processTransferOperation(String login, @Valid TransferRequest request) {
        log.info("Начало обработки перевода для пользователя: {}, запрос: {}", login, request);

        try {
            log.info("Получаем аккаунт отправителя: {}", login);
            final var fromAccount = accountClient.getUserAccount(login);

            log.info("Получаем баланс отправителя для валюты: {}", request.fromCurrency());
            final var fromBalance = getBalance(fromAccount, request.fromCurrency());

            if (fromBalance.balance().compareTo(request.amount()) < 0) {
                log.error("Недостаточно средств: {} < {}", fromBalance.balance(), request.amount());
                throw new TransferOperationException("Недостаточно средств на счете " + fromBalance.currency());
            }

            log.info("Получаем аккаунт получателя: {}", request.toLogin());
            final var toAccount = accountClient.getUserAccount(request.toLogin());

            log.info("Получаем баланс получателя для валюты: {}", request.toCurrency());
            final var toBalance = getBalance(toAccount, request.toCurrency());

            BigDecimal convertedAmount = request.amount();
            if (!fromBalance.currency().equals(toBalance.currency())) {
                log.info("Конвертируем валюту: {} {} -> {} {}",
                        request.amount(), request.fromCurrency(),
                        request.toCurrency(), request.toCurrency());
                convertedAmount = exchangeClient.convert(request.fromCurrency(), request.toCurrency(), request.amount());
            } else {
                log.info("Конвертация не требуется - валюты одинаковые");
            }

            log.info("Списываем с {}: {} {} -> {} {}",
                    login, fromBalance.balance(), fromBalance.currency(),
                    fromBalance.balance().subtract(convertedAmount), fromBalance.currency());
            log.info("Зачисляем на {}: {} {} -> {} {}",
                    request.toLogin(), toBalance.balance(), toBalance.currency(),
                    toBalance.balance().add(convertedAmount), toBalance.currency());

            try {
                accountClient.updateBalance(login, request.fromCurrency(), fromBalance.balance().subtract(convertedAmount));
                accountClient.updateBalance(request.toLogin(), request.toCurrency(), toBalance.balance().add(convertedAmount));
                log.info("Перевод успешно выполнен!");
            } catch (Exception e) {
                log.error("Ошибка при выполнении перевода: {}", e.getMessage(), e);
                throw new RuntimeException("Ошибка при выполнении перевода", e);
            }

        } catch (TransferOperationException e) {
            log.error("Ошибка бизнес-логики перевода: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обработке перевода: {}", e.getMessage(), e);
            throw new RuntimeException("Неожиданная ошибка при обработке перевода", e);
        }
    }

    private AccountBalanceDto getBalance(UserAccountDto account, String fromCurrency) {
        return account.balances().stream()
                .filter(b -> b.currency().name().equals(fromCurrency) && b.isExists())
                .findFirst()
                .orElseThrow(() -> new TransferOperationException("Счёт в валюте " + fromCurrency + " не существует"));
    }
}
