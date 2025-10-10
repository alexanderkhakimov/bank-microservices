package com.bank.frontui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/logout-page")
public class LogoutController {

    @GetMapping
    public Mono<String> getLogoutPage() {
        return Mono.just("logout");
    }
}
