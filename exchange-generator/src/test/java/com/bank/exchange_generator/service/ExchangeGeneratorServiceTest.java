package com.bank.exchange_generator.service;

import com.bank.kafka.enums.Currency;
import com.bank.kafka.event.ExchangeRateUpdateRequested;
import com.bank.kafka.producer.KafkaMessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeGeneratorServiceTest {

    @Mock
    private KafkaMessageProducer<ExchangeRateUpdateRequested> kafkaProducer;

    @Captor
    private ArgumentCaptor<ExchangeRateUpdateRequested> eventCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    private ExchangeGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new ExchangeGeneratorService(kafkaProducer);
    }

    @Test
    void shouldGenerateAndPublishRatesToKafka() {
        service.updateRates();

        verify(kafkaProducer, times(3))
                .publish(eq("exchange-requests"), anyString(), eventCaptor.capture());

        List<ExchangeRateUpdateRequested> publishedEvents = eventCaptor.getAllValues();

        assertThat(publishedEvents).hasSize(3);

        var rubEvent = findByCurrency(publishedEvents, Currency.RUB);
        assertThat(rubEvent.value()).isEqualByComparingTo(BigDecimal.ONE);

        var eurEvent = findByCurrency(publishedEvents, Currency.EUR);
        assertThat(eurEvent.value())
                .isGreaterThanOrEqualTo(BigDecimal.valueOf(0.011))
                .isLessThanOrEqualTo(BigDecimal.valueOf(10.011));

        var usdEvent = findByCurrency(publishedEvents, Currency.USD);
        assertThat(usdEvent.value())
                .isGreaterThanOrEqualTo(BigDecimal.valueOf(0.013))
                .isLessThanOrEqualTo(BigDecimal.valueOf(10.013));

        verify(kafkaProducer).publish(eq("exchange-requests"), eq("RUB"), any());
        verify(kafkaProducer).publish(eq("exchange-requests"), eq("EUR"), any());
        verify(kafkaProducer).publish(eq("exchange-requests"), eq("USD"), any());
    }

    private ExchangeRateUpdateRequested findByCurrency(List<ExchangeRateUpdateRequested> events, Currency currency) {
        return events.stream()
                .filter(e -> e.currency() == currency)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No event for currency: " + currency));
    }
}