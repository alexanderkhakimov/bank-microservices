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
                new ExchangeRate("Доллар", "USD", 90.0 + random.nextDouble() * 10.0), // 90–100 RUB
                new ExchangeRate("Евро", "EUR", 100.0 + random.nextDouble() * 10.0)  // 100–110 RUB
        );
        currentRates.set(rates);
    }

}
