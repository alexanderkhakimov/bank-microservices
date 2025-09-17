package com.bank.exchange.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/getInfo")
public class ExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);
    @GetMapping
    public ResponseEntity<String> getInfo() {
        logger.info("Запрос получен в Обмен сервисе");
        return ResponseEntity.ok("EXCHANGE!");
    }
}