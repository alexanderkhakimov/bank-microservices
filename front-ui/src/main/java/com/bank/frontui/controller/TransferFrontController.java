package com.bank.frontui.controller;

import com.bank.frontui.dto.TransferRequest;
import com.bank.frontui.exception.TransferOperationException;
import com.bank.frontui.service.TransferFrontService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/{login}/transfer")
public class TransferFrontController {
    private final TransferFrontService transferFrontService;

    @PostMapping
    public Mono<String> processTransfer(
            @PathVariable String login,
            @Valid @ModelAttribute TransferRequest request,
            BindingResult bindingResult,
            Model model) {
        log.info("Запрос {} передан в сервис", request);
        if (bindingResult.hasErrors()) {
            final var errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();
            model.addAttribute("transferErrors", errors);
            return Mono.just("redirect:/main");
        }
        return transferFrontService.processTransfer(request, login)
                .doOnSuccess(result -> {
                    log.info("Запрос {} выполнен успешно", request);
                    model.addAttribute("successMessage", "Перевод выполнен успешно!");
                })
                .then(Mono.just("redirect:/main"))
                .onErrorResume(TransferOperationException.class, e -> {
                    log.warn("Запрос {} выполнен с ошибкой: {}", request, e.getMessage());
                    model.addAttribute("transferErrors", List.of(e.getMessage()));
                    return Mono.just("redirect:/main");
                });
    }
}
