//package com.bank.frontui.controller;
//
//import com.bank.frontui.dto.CashFormRequest;
//import com.bank.frontui.exception.CashOperationException;
//import com.bank.frontui.model.Currency;
//import com.bank.frontui.service.CashFrontService;
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/user/{login}/cash")
//public class CashFrontController {
//    private static final Logger logger = LoggerFactory.getLogger(CashFrontController.class);
//    private final CashFrontService cashFrontService;
//
//    public CashFrontController(CashFrontService cashFrontService) {
//        this.cashFrontService = cashFrontService;
//    }
//
//    @PostMapping
//    public String processCashOperation(
//            @PathVariable String login,
//            @Valid @ModelAttribute CashFormRequest request,
//            BindingResult bindingResult,
//            Model model) {
//        logger.info("Запрос {} пользователя: {} получен", login, request);
//
//        model.addAttribute("currency", Currency.values());
//
//        if (bindingResult.hasErrors()) {
//            final var errors = bindingResult.getFieldErrors().stream()
//                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                    .toList();
//            model.addAttribute("cashErrors", errors);
//            return "redirect:/";
//        }
//
//        try {
//            cashFrontService.processCashOperation(login, request).block();
//            model.addAttribute("successMessage", "Операция выполнена успешно");
//        } catch (CashOperationException e) {
//            model.addAttribute("cashErrors", List.of(e.getMessage()));
//        }
//        return "redirect:/";
//    }
//}
