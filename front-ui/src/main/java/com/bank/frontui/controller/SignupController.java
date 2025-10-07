package com.bank.frontui.controller;

import com.bank.frontui.dto.UserRegistrationRequest;
import com.bank.frontui.service.AccountClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@RequestMapping("/signup")
@Controller
public class SignupController {
    public final AccountClient accountClient;

    public SignupController(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @GetMapping
    public Mono<String> getFormSignup() {
        return Mono.just("signup");
    }

    @PostMapping
    public Mono<String> signup(
            Model model,
            @Valid UserRegistrationRequest request) {
        log.info("Запрос на регистрацию пользователя {}", request);
        if (!request.password().equals(request.confirmPassword())) {
            model.addAttribute("errors", "Пароли не совпадают");
            return Mono.just("signup");
        }
        if (Period.between(request.birthdate(), LocalDate.now()).getYears() < 18) {
            model.addAttribute("errors", "Возраст должен быть старше 18");
            return Mono.just("signup");
        }
        return accountClient.register(request)
                .then(Mono.just("/redirect:/main"))
                .doOnError(error -> {
                    model.addAttribute("errors", "Ошибка регистрации");
                    log.error("Возникла ошибка при регистрации пользователя {}, {}", request.login(), error.getMessage());
                });
    }
}
