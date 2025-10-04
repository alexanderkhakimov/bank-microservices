//package com.bank.frontui.controller;
//
//import com.bank.frontui.dto.ExchangeRate;
//import com.bank.frontui.service.ProxyService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/proxy")
//public class ProxyController {
//    @Autowired
//    private ProxyService proxyService;
//
//    @GetMapping("/rates")
//    ResponseEntity<List<ExchangeRate>> getRates() {
//        return ResponseEntity.ok(proxyService.getRates());
//    }
//
//}
