package com.bank.exchange_generator.service;

import com.bank.exchange_generator.dto.UpdateRateRequestDto;
import com.bank.exchange_generator.enums.Currency;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ExchangeGeneratorServiceTest {
    @Mock
    private ExchangeClient exchangeClient;
    @Captor
    private ArgumentCaptor<List<UpdateRateRequestDto>> ratesCaptor;

    private ExchangeGeneratorService exchangeGeneratorService;

    @BeforeEach
    void setUp() {
        exchangeGeneratorService = new ExchangeGeneratorService(exchangeClient);
    }

    @Test
    void shouldGenerateAndSendRatesWhenUpdateRatesCalled() {
        exchangeGeneratorService.updateRates();

        verify(exchangeClient, times(1)).updateRates(ratesCaptor.capture());

        final var captureRates = ratesCaptor.getValue();

        assertThat(captureRates).hasSize(3);

        final var rateRub = captureRates.stream()
                .filter(r -> r.getCurrency().equals(Currency.RUB))
                .findFirst()
                .orElseThrow();
        assertThat(rateRub.getValue()).isEqualByComparingTo(BigDecimal.ONE);

        final var rateUSD = captureRates.stream()
                .filter(r -> r.getCurrency().equals(Currency.RUB))
                .findFirst()
                .orElseThrow();
        assertThat(rateUSD.getValue()).isBetween(BigDecimal.valueOf(0.011), BigDecimal.valueOf(10.011));

    }

}
