package com.bank.exchange.controller;

import com.bank.kafka.events.RateUpdateEvent;
import com.bank.kafka.producer.KafkaMessageProducer;
import com.bank.exchange_generator.enums.Currency;
import com.bank.exchange.repository.RateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class ExchangeKafkaToDbIT extends KafkaBaseIT {

    @Autowired
    private KafkaMessageProducer<RateUpdateEvent> kafkaProducer;

    @Autowired
    private RateRepository rateRepository;

    @Test
    void shouldUpdateDatabaseFromKafka() {
        // Given
        var event = new RateUpdateEvent(Currency.USD, BigDecimal.valueOf(75.5));

        kafkaProducer.publish("exchange-requests", "USD", event);

        await().atMost(10, SECONDS).untilAsserted(() -> {
            var rate = rateRepository.findByCurrency(Currency.USD);
            assert rate.isPresent() : "Rate for USD not found";
            assert rate.get().getValue().compareTo(BigDecimal.valueOf(75.5)) == 0
                    : "Expected 75.5, got " + rate.get().getValue();
        });
    }
}