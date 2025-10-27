package com.bank.exchange_generator.service;

import com.bank.exchange_generator.dto.UpdateRateRequestDto;
import com.bank.exchange_generator.enums.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@EnableScheduling
@Slf4j
public class ExchangeGeneratorService {
    private final ExchangeClient exchangeClient;
    private final Random random;

    public ExchangeGeneratorService(ExchangeClient exchangeClient) {
        log.info("ExchangeGeneratorService инициализирован");
        this.exchangeClient = exchangeClient;
        this.random = new Random();
    }

    @Scheduled(fixedRate = 10000)
    public void updateRates() {
        try {
            var rates = List.of(
                    UpdateRateRequestDto.builder().currency(Currency.RUB).value(BigDecimal.ONE).build(),
                    UpdateRateRequestDto.builder().currency(Currency.EUR).value(BigDecimal.valueOf(0.011 + random.nextDouble() * 10.0)).build(),
                    UpdateRateRequestDto.builder().currency(Currency.USD).value(BigDecimal.valueOf(0.013 + random.nextDouble() * 10.0)).build()
            );
            exchangeClient.updateRates(rates);
            log.info("Rates: {} отправлен.", rates);
        } catch (Exception e) {
            log.error("Ошибка в Scheduler {}", e.getMessage());
        }
    }


}
