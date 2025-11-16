package com.bank.exchange.service;

import com.bank.exchange.dto.RateResponseDto;
import com.bank.exchange.dto.RateUiResponseDto;
import com.bank.exchange.dto.UpdateRateRequestDto;
import com.bank.exchange.model.Rate;
import com.bank.exchange.repository.RateRepository;
import com.bank.kafka.event.ExchangeRateUpdateRequested;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateService {
    private final RateRepository rateRepository;
    @Transactional
    public void updateRateFromKafka(ExchangeRateUpdateRequested event) {
        log.info("Kafka → обновление курса: {} = {}", event.currency(), event.value());

        Rate rate = rateRepository.findByCurrency(event.currency())
                .orElse(Rate.builder().currency(event.currency()).build());

        rate.setValue(event.value());
        rateRepository.save(rate);
    }
    @Transactional
    public void updateAll(List<UpdateRateRequestDto> dto) {
        final var rate = dto.stream()
                .map(r -> {
                    return Rate.builder()
                            .value(r.getValue())
                            .currency(r.getCurrency())
                            .build();
                }).toList();
        rateRepository.deleteAll();
        rateRepository.saveAll(rate);
    }

    public List<RateUiResponseDto> getUiRatesAll() {
        return rateRepository.findAll().stream()
                .map(rate -> RateUiResponseDto.builder()
                        .title(rate.getCurrency().getTitle())
                        .name(rate.getCurrency().name())
                        .value(rate.getValue())
                        .build())
                .toList();
    }

    public List<RateResponseDto> getRatesAll() {
        return rateRepository.findAll().stream()
                .map(rate -> RateResponseDto.builder()
                        .currency(rate.getCurrency())
                        .value(rate.getValue())
                        .build())
                .toList();
    }
}
