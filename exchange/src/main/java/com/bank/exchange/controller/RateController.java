package com.bank.exchange.controller;

import com.bank.exchange.dto.RateResponseDto;
import com.bank.exchange.dto.RateUiResponseDto;
import com.bank.exchange.dto.UpdateRateRequestDto;
import com.bank.exchange.model.Rate;
import com.bank.exchange.service.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
@Slf4j
public class RateController {
    private final RateService rateService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/")
    public List<RateUiResponseDto> getRatesUi() {
        return rateService.getRatesAll().stream()
                .map(rate -> RateUiResponseDto.builder()
                        .title(rate.getCurrency().getTitle())
                        .name(rate.getCurrency().name())
                        .value(rate.getValue())
                        .build())
                .toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/rates")
    public List<RateResponseDto> getRates() {
        return rateService.getRatesAll().stream()
                .map(rate -> RateResponseDto.builder()
                        .currency(rate.getCurrency())
                        .value(rate.getValue())
                        .build())
                .toList();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public void create(@RequestBody List<UpdateRateRequestDto> dto) {
        log.info("Получен запрос в сервис обмена {}", dto);
        final var rate = dto.stream()
                .map(r -> {
                    return Rate.builder()
                            .value(r.getValue())
                            .currency(r.getCurrency())
                            .build();
                }).toList();
        rateService.updateAll(rate);
    }
}
