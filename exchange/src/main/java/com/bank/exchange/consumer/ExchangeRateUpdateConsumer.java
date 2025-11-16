package com.bank.exchange.consumer;

import com.bank.exchange.service.RateService;
import com.bank.kafka.consumer.KafkaMessageConsumer;
import com.bank.kafka.event.ExchangeRateUpdateRequested;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateUpdateConsumer extends KafkaMessageConsumer<ExchangeRateUpdateRequested> {

    private final RateService rateService;

    @Override
    protected void process(ExchangeRateUpdateRequested event) {
        rateService.updateRateFromKafka(event);
    }

    @Override
    protected Class<ExchangeRateUpdateRequested> getEventType() {
        return ExchangeRateUpdateRequested.class;
    }
}