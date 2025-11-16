package com.bank.exchange_generator.service;

import com.bank.kafka.enums.Currency;
import com.bank.kafka.event.ExchangeRateUpdateRequested;
import com.bank.kafka.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ExchangeGeneratorService {

    private final KafkaMessageProducer<ExchangeRateUpdateRequested> producer;
    private final Random random = new Random();

    @Scheduled(fixedRate = 10000)
    public void updateRates() {
        try {
            List.of(
                    new ExchangeRateUpdateRequested(Currency.RUB, BigDecimal.ONE),
                    new ExchangeRateUpdateRequested(Currency.EUR, BigDecimal.valueOf(0.011 + random.nextDouble() * 10.0)),
                    new ExchangeRateUpdateRequested(Currency.USD, BigDecimal.valueOf(0.013 + random.nextDouble() * 10.0))
            ).forEach(event -> {
                String key = event.currency().name();
                producer.publish("exchange-requests", key, event);
                log.info("Опубликовано: {} = {}", key, event.value());
            });
        } catch (Exception e) {
            log.error("Ошибка генерации курсов", e);
        }
    }
}
