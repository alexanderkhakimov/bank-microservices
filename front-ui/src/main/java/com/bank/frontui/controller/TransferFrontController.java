package com.bank.frontui.controller;

import com.bank.frontui.dto.TransferRequest;
import com.bank.frontui.exception.CashOperationException;
import com.bank.frontui.exception.TransferOperationException;
import com.bank.frontui.service.TransferFrontService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/{login}/transfer")
public class TransferFrontController {
   private final TransferFrontService transferFrontService;

    @PostMapping
    public String processTransfer(
            @PathVariable String login,
            @Valid @ModelAttribute TransferRequest request,
            BindingResult bindingResult,
            Model model) {
        try {
            log.info("Запрос {} передан в сервис", request);
            transferFrontService.processTransfer(request, login).block();
            log.info("Запрос {} выполнен успешно", request);
            return "redirect:/";
        } catch (TransferOperationException e) {
            model.addAttribute("transferErrors", List.of(e.getMessage()));
            log.info("Запрос {} выполнен с ошибкой {}", request, e.getMessage());
        }
        return "redirect:/";
    }
}
