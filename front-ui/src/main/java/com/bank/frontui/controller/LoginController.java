package com.bank.frontui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public Mono<String> getLoginPage(){
        log.info("Предоставлен доступ к странице логина");
        return Mono.just("login");
    }
}
