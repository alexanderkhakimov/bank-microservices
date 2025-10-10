package com.bank.frontui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public Mono<String> getLoginPage(@RequestParam(required = false) String error,
                                     @RequestParam(required = false) String logout,
                                     Model model) {
        if (error != null) {
            model.addAttribute("error", "Ошибка аутентификации");
        } else if (logout != null) {
            model.addAttribute("logout", "Вы успешно вышли из системы");
        }
        log.info("Предоставлен доступ к странице логина");
        return Mono.just("login");
    }
}
