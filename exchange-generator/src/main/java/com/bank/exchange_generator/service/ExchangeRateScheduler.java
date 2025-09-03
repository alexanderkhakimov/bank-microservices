package com.bank.exchange_generator.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ExchangeRateScheduler {
    private final ExchangeGeneratorService exchangeGeneratorService;

    public ExchangeRateScheduler(ExchangeGeneratorService exchangeGeneratorService) {
        this.exchangeGeneratorService = exchangeGeneratorService;
    }

    @Scheduled(fixedRate = 1000)
    void updateRates() {
        exchangeGeneratorService.updateExchangeRate();
    }
}
