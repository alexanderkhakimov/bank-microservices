package com.bank.exchange.controller;

import com.bank.exchange.dto.TransferRequest;
import com.bank.exchange.service.ExchangeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/convert")
public class ExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping
    public ResponseEntity<Double> convertAmount(@RequestBody @Valid TransferRequest request) {
        logger.info("Запрос {} получен в Обмен сервисе", request);
        final var rate = exchangeService.convert(request);
        return ResponseEntity.ok(rate);
    }
}