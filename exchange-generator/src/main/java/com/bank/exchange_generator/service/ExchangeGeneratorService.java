package com.bank.exchange_generator.service;

import com.bank.exchange_generator.model.ExchangeRate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ExchangeGeneratorService {
    private final Random random = new Random();
    private final AtomicReference<List<ExchangeRate>> currentRates = new AtomicReference<>();

    public ExchangeGeneratorService() {
        updateExchangeRate();
    }

    public List<ExchangeRate> getCurrentRates() {
        return currentRates.get();
    }

    public void updateExchangeRate() {
        List<ExchangeRate> rates = List.of(
                new ExchangeRate("Рубль", "RUB", 1.0),
                new ExchangeRate("Доллар", "USD", 0.13 + random.nextDouble() * 10.0),
                new ExchangeRate("Евро", "EUR", 0.011 + random.nextDouble() * 10.0)
        );
        currentRates.set(rates);
    }

}
