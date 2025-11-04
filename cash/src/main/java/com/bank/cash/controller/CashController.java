package com.bank.cash.controller;

import com.bank.cash.dto.CashRequest;
import com.bank.cash.service.CashService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cash")
public class CashController {
    private final CashService cashService;

    @PostMapping("/user/{login}/cash")
    public ResponseEntity<String> processCashOperation(
            @PathVariable String login,
            @Valid @RequestBody CashRequest cashRequest
    ) {
        log.info("Запрос от пользователя с логином {}: {}", login, cashRequest);
        cashService.processCashOperation(login, cashRequest);
        log.info("Запрос успешно выполнен для пользователя с логином {}", login);
        return ResponseEntity.ok().build();
    }
}
