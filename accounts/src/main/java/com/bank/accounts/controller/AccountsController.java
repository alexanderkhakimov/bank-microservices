package com.bank.accounts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AccountsController {
    @GetMapping("api/check")
    @ResponseBody
    String getCheck() {
        return "IT'S WORK!";
    }
}
