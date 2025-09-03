package com.bank.exchange_generator.controller;

import com.bank.exchange_generator.model.ExchangeRate;
import com.bank.exchange_generator.service.ExchangeGeneratorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/rates")
public class ExchangeGeneratorController {

    private final ExchangeGeneratorService exchangeGeneratorService;

    public ExchangeGeneratorController(ExchangeGeneratorService exchangeGeneratorService) {
        this.exchangeGeneratorService = exchangeGeneratorService;
    }

    @GetMapping
    public List<ExchangeRate> getRates() {
        return exchangeGeneratorService.getCurrentRates();
    }
}
