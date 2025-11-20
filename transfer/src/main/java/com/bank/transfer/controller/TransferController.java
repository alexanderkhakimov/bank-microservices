package com.bank.transfer.controller;

import com.bank.transfer.TransferService.TransferService;
import com.bank.transfer.dto.TransferRequest;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;
    private final Counter transferRequestsCounter;

    public TransferController(TransferService transferService, MeterRegistry meterRegistry) {
        this.transferService = transferService;
        this.meterRegistry = meterRegistry;
        this.transferRequestsCounter = Counter.builder("bank.transfer.requests")
                .description("Total transfer requests")
                .register(meterRegistry);
    }

    @PostMapping("/user/{login}/transfer")
    @Timed(value = "bank.transfer.request", description = "Transfer request processing time")
    public ResponseEntity<String> processTransferOperation(
            @PathVariable String login,
            @Valid @RequestBody TransferRequest request
    ) {
        transferRequestsCounter.increment();

        log.info("Запрос от пользователя с логином {}: {}", login, request);
        transferService.processTransferOperation(login, request);
        log.info("Запрос успешно выполнен для пользователя с логином {}", login);
        return ResponseEntity.ok().build();
    }
}
