package com.bank.transfer.controller;

import com.bank.transfer.TransferService.TransferService;
import com.bank.transfer.dto.TransferRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/user/{login}/transfer")
    public ResponseEntity<String> processTransferOperation(
            @PathVariable String login,
            @Valid @RequestBody TransferRequest request
    ) {
        log.info("Запрос от пользователя с логином {}: {}", login, request);
        transferService.processTransferOperation(login, request);
        log.info("Запрос успешно выполнен для пользователя с логином {}", login);
        return ResponseEntity.ok().build();
    }
}
