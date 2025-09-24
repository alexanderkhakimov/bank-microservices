package com.bank.exchange_generator.service;

import com.bank.exchange_generator.dto.UpdateRateRequestDto;
import com.bank.exchange_generator.enums.Currency;
import com.bank.exchange_generator.model.ExchangeRate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ExchangeGeneratorService {
    final private ExchangeClient exchangeClient;
    private final Random random = new Random();
    private final AtomicReference<List<ExchangeRate>> currentRates = new AtomicReference<>();

    public ExchangeGeneratorService(ExchangeClient exchangeClient) {
        this.exchangeClient = exchangeClient;
        updateExchangeRate();
    }

    public List<ExchangeRate> getCurrentRates() {
        return currentRates.get();
    }

    public void updateExchangeRate() {
        final var rates = List.of(
                new ExchangeRate("Рубль", "RUB", 1.0),
                new ExchangeRate("Доллар", "USD", 0.13 + random.nextDouble() * 10.0),
                new ExchangeRate("Евро", "EUR", 0.011 + random.nextDouble() * 10.0)
        );
        currentRates.set(rates);
    }
    @Scheduled(fixedRate = 1000)
    public void updateRates(){
        var rates = List.of(
                UpdateRateRequestDto.builder().currency(Currency.EUR).value(BigDecimal.valueOf(0.011 + random.nextDouble() * 10.0)).build(),
                UpdateRateRequestDto.builder().currency(Currency.USD).value(BigDecimal.valueOf(0.013 + random.nextDouble() * 10.0)).build()
                );
        exchangeClient.updateRates(rates);
    }


}
