package com.bank.frontui.controller;

import com.bank.frontui.dto.CashFormRequest;
import com.bank.frontui.exception.CashOperationException;
import com.bank.frontui.model.Currency;
import com.bank.frontui.service.CashFrontService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/{login}/cash")
public class CashFrontController {
    private final CashFrontService cashFrontService;

    @PostMapping
    public Mono<String> processCashOperation(
            @PathVariable String login,
            @Valid @ModelAttribute CashFormRequest request,
            BindingResult bindingResult,
            Model model) {
        log.info("Запрос {} пользователя: {} получен", login, request);

        model.addAttribute("currency", Currency.values());

        if (bindingResult.hasErrors()) {
            final var errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .toList();
            model.addAttribute("cashErrors", errors);
            return Mono.just("main");
        }
        return cashFrontService.processCashOperation(request, login)
                .doOnSuccess(result -> log.info("Запрос {} выполнен успешно", request))
                .then(Mono.just("main"))
                .onErrorResume(CashOperationException.class, e -> {
                    log.warn("Запрос {} выполнен с ошибкой: {}", request, e.getMessage());
                    model.addAttribute("cashErrors", List.of(e.getMessage()));
                    return Mono.just("main");
                });
    }
}
