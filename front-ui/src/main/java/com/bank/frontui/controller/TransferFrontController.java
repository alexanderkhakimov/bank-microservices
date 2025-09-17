package com.bank.frontui.controller;

import com.bank.frontui.service.TransferFrontService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{login}/transfer")
public class TransferFrontController {
    private static final Logger logger = LoggerFactory.getLogger(TransferFrontController.class);
    private final TransferFrontService transferFrontService;

    public TransferFrontController(TransferFrontService transferFrontService) {
        this.transferFrontService = transferFrontService;
    }

    @GetMapping
    public String transfer(@PathVariable String login) {

        try {
            logger.info("Запрос передан в сервис");
            var info = transferFrontService.getInfo();
            return "Перевод осущенствлен " + login + " %s!".formatted(info);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
