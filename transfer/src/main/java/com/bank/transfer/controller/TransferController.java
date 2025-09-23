package com.bank.transfer.controller;

import com.bank.transfer.TransferService.TransferService;
import com.bank.transfer.dto.TransferRequest;
import jakarta.validation.Valid;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/{login}/transfer")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    @PostMapping
    public ResponseEntity<String> processTransferOperation(
            @PathVariable String login,
            @Valid @RequestBody TransferRequest request,
            BindingResult bindingResult
    ) {
        logger.info("Запрос от пользователя с логином {}: {}", login, request);
        transferService.processTransferOperation(login, request);
        logger.info("Запрос успешно выполнен для пользователя с логином {}", login);
        return ResponseEntity.ok().build();
    }
}
