package com.bank.frontui.controller;

import com.bank.frontui.dto.RegistrationUserForm;
import com.bank.frontui.mapper.UserMapper;
import com.bank.frontui.service.AccountClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;

@Slf4j
@RequestMapping("/signup")
@RequiredArgsConstructor
@Controller
public class SignupController {
    public final AccountClient accountClient;
    private final UserMapper userMapper;

    @GetMapping
    public Mono<String> getFormSignup() {
        return Mono.just("signup");
    }

    @PostMapping
    public Mono<String> signup(
            Model model,
            @Valid RegistrationUserForm userForm) {
        log.info("Запрос на регистрацию пользователя {}", userForm);
        if (!userForm.password().equals(userForm.confirmPassword())) {
            model.addAttribute("errors", "Пароли не совпадают");
            return Mono.just("signup");
        }
        if (Period.between(userForm.birthdate(), LocalDate.now()).getYears() < 18) {
            model.addAttribute("errors", "Возраст должен быть старше 18");
            return Mono.just("signup");
        }

        final var requestDto = userMapper.toRegisterUserRequestDtoWithEncodedPassword(userForm);

        return accountClient.register(requestDto)
                .thenReturn("redirect:/main")
                .onErrorResume(error -> {
                    model.addAttribute("errors", "Ошибка регистрации");
                    log.error("Возникла ошибка при регистрации пользователя {}, {}", userForm.login(), error.getMessage());
                    return Mono.just("signup");
                });
    }
}