package com.bank.frontui.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/signup")
    public String signupForm(Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String login, @RequestParam String password,
                         @RequestParam String confirm_password, @RequestParam String name,
                         @RequestParam String birthdate, Model model) {
        // Валидация: Проверь пароли совпадают, возраст >18, все поля заполнены
        // Если ошибка, добавь в model "errors" и верни "signup"
        // Иначе создай пользователя (пока заглушка), аутентифицируй и редирект на "/"
        if (!password.equals(confirm_password)) {
            model.addAttribute("errors", "Пароли не совпадают");
            return "signup";
        }
        // ... Другая валидация (используй @Valid позже)
        // userService.registerUser(...);
        return "redirect:/";
    }

    @GetMapping("/")
    public String mainPage(Authentication authentication, Model model) {
        String login = authentication.getName();  // Текущий логин
        model.addAttribute("login", login);
        // Добавь атрибуты: accounts, currency, users и т.д. (заглушки пока)
        // model.addAttribute("accounts", ...);
        // model.addAttribute("currency", ...);
        return "main";
    }

    // Добавь другие POST для форм: editPassword, cash, transfer и т.д.
    // Например:
    @PostMapping("/user/{login}/editPassword")
    public String editPassword(@RequestParam String password, @RequestParam String confirm_password, Model model) {
        // Валидация и обновление
        return "redirect:/";
    }
}
