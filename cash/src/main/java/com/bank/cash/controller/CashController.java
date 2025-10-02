package com.bank.cash.controller;

import com.bank.cash.dto.CashRequest;
import com.bank.cash.service.CashService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{login}/cash")
public class
CashController {
    private static final Logger logger = LoggerFactory.getLogger(CashController.class);
    private final CashService cashService;

    public CashController(CashService cashService) {
        this.cashService = cashService;
    }

    @PostMapping
    public ResponseEntity<List<String>> processCashOperation(
            @PathVariable String login,
            @Valid @RequestBody CashRequest cashRequest
    ) {
        logger.info("Запрос от пользователя с логином {}: {}", login, cashRequest);
        cashService.processCashOperation(login, cashRequest);
        logger.info("Запрос успешно выполнен для пользователя с логином {}", login);
        return ResponseEntity.ok().build();
    }
}
