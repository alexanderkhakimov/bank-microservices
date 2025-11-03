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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/{login}/cash")
public class CashFrontController {
    private final CashFrontService cashFrontService;

    @PostMapping
    public String processCashOperation(
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
            return "redirect:/";
        }

        try {
            cashFrontService.processCashOperation(login, request).block();
            model.addAttribute("successMessage", "Операция выполнена успешно");
        } catch (CashOperationException e) {
            model.addAttribute("cashErrors", List.of(e.getMessage()));
        }
        return "redirect:/";
    }
}
