package com.bank.transfer.TransferService;

import com.bank.transfer.dto.AccountBalanceDto;
import com.bank.transfer.dto.TransferRequest;
import com.bank.transfer.dto.UserAccountDto;
import com.bank.transfer.exception.TransferOperationException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransferService {
    private final AccountClient accountClient;
    private final ExchangeClient exchangeClient;
    private final MeterRegistry meterRegistry;

    private final Counter successfulTransfers;
    private final Counter failedTransfers;
    private final Counter suspiciousOperations;
    private final Timer transferProcessingTime;

    public TransferService(AccountClient accountClient, ExchangeClient exchangeClient, MeterRegistry meterRegistry) {
        this.accountClient = accountClient;
        this.exchangeClient = exchangeClient;
        this.meterRegistry = meterRegistry;

        this.successfulTransfers = Counter.builder("bank.transfer.success")
                .description("Successful money transfers")
                .register(meterRegistry);

        this.failedTransfers = Counter.builder("bank.transfer.failed")
                .description("Failed money transfers")
                .register(meterRegistry);

        this.suspiciousOperations = Counter.builder("bank.transfer.suspicious.blocked")
                .description("Blocked suspicious transfers")
                .register(meterRegistry);

        this.transferProcessingTime = Timer.builder("bank.transfer.processing.time")
                .description("Transfer processing duration")
                .register(meterRegistry);
    }

    public void processTransferOperation(String login, @Valid TransferRequest request) {
        transferProcessingTime.record(() -> {
            log.info("Начало обработки перевода для пользователя: {}, запрос: {}", login, request);

            try {
                log.info("Получаем аккаунт отправителя: {}", login);
                final var fromAccount = accountClient.getUserAccount(login);

                log.info("Получаем баланс отправителя для валюты: {}", request.fromCurrency());
                final var fromBalance = getBalance(fromAccount, request.fromCurrency());

                if (fromBalance.balance().compareTo(request.amount()) < 0) {
                    log.error("Недостаточно средств: {} < {}", fromBalance.balance(), request.amount());
                    failedTransfers.increment();
                    throw new TransferOperationException("Недостаточно средств на счете " + fromBalance.currency());
                }

                if (isSuspiciousOperation(login, request)) {
                    log.warn("Обнаружена подозрительная операция: {} -> {}", login, request.toLogin());
                    suspiciousOperations.increment(); // МЕТРИКА: подозрительная операция
                    throw new TransferOperationException("Операция заблокирована по подозрению в мошенничестве");
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
                    successfulTransfers.increment();

                } catch (Exception e) {
                    log.error("Ошибка при выполнении перевода: {}", e.getMessage(), e);
                    failedTransfers.increment();
                    throw new RuntimeException("Ошибка при выполнении перевода", e);
                }

            } catch (TransferOperationException e) {
                log.error("Ошибка бизнес-логики перевода: {}", e.getMessage());
                failedTransfers.increment();
                throw e;
            } catch (Exception e) {
                log.error("Неожиданная ошибка при обработке перевода: {}", e.getMessage(), e);
                failedTransfers.increment();
                throw new RuntimeException("Неожиданная ошибка при обработке перевода", e);
            }
        });
    }

    private boolean isSuspiciousOperation(String fromLogin, TransferRequest request) {
        if (fromLogin.equals(request.toLogin())) {
            return true;
        }
        if (request.amount().compareTo(new BigDecimal("1000000")) > 0) {
            return true;
        }

        return false;
    }

    private AccountBalanceDto getBalance(UserAccountDto account, String fromCurrency) {
        return account.balances().stream()
                .filter(b -> b.currency().name().equals(fromCurrency) && b.isExists())
                .findFirst()
                .orElseThrow(() -> {
                    failedTransfers.increment();
                    return new TransferOperationException("Счёт в валюте " + fromCurrency + " не существует");
                });
    }
}
