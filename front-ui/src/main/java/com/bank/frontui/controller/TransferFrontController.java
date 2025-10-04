//package com.bank.frontui.controller;
//
//import com.bank.frontui.dto.TransferRequest;
//import com.bank.frontui.exception.CashOperationException;
//import com.bank.frontui.exception.TransferOperationException;
//import com.bank.frontui.service.TransferFrontService;
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/user/{login}/transfer")
//public class TransferFrontController {
//    private static final Logger logger = LoggerFactory.getLogger(TransferFrontController.class);
//    private final TransferFrontService transferFrontService;
//
//    public TransferFrontController(TransferFrontService transferFrontService) {
//        this.transferFrontService = transferFrontService;
//    }
//
//    @PostMapping
//    public String processTransfer(
//            @PathVariable String login,
//            @Valid @ModelAttribute TransferRequest request,
//            BindingResult bindingResult,
//            Model model) {
//        try {
//            logger.info("Запрос {} передан в сервис", request);
//            transferFrontService.processTransfer(request, login).block();
//            logger.info("Запрос {} выполнен успешно", request);
//            return "redirect:/";
//        } catch (TransferOperationException e) {
//            model.addAttribute("transferErrors", List.of(e.getMessage()));
//            logger.info("Запрос {} выполнен с ошибкой {}", request, e.getMessage());
//        }
//        return "redirect:/";
//    }
//}
