package com.bank.exchange.controller;

import com.bank.exchange.dto.UpdateRateRequestDto;
import com.bank.exchange.model.Rate;
import com.bank.exchange.service.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class RateController {
    private final RateService rateService;

    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody List<UpdateRateRequestDto> dto) {
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
