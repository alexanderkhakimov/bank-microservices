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
        log.info("Получен запрос в сервис обмена от Front-UI сервиса");
        return rateService.getUiRatesAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/rates")
    public List<RateResponseDto> getRates() {
        log.info("Получен запрос в сервис обмена от внутреннего сервиса");
        return rateService.getRatesAll();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public void create(@RequestBody List<UpdateRateRequestDto> dto) {
        log.info("Получен запрос в сервис обмена {}", dto);
        rateService.updateAll(dto);
    }
}
