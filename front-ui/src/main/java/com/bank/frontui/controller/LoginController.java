package com.bank.frontui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public String getLoginPage(){
        log.info("Предоставлен доступ к странице логина");
        return "login";
    }
}
