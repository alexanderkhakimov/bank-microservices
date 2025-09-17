package com.bank.transfer.controller;

import com.bank.transfer.TransferService.ExchangeClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/getInfo")
public class TransferController {
    private final ExchangeClientService exchangeClientService;

    public TransferController(ExchangeClientService exchangeClientService) {
        this.exchangeClientService = exchangeClientService;
    }

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    @GetMapping
    public ResponseEntity<String> getInfo() {
        logger.info("Запрос получен в Трансфер сервисе");
        var info = exchangeClientService.getInfoFromExchange();
        logger.info("Запрос получен из Обмена **{}** в Трансфер сервисе", info);
        return ResponseEntity.ok("GOOD " + info + "!");
    }
}
