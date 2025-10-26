package com.bank.frontui.controller;

import com.bank.frontui.dto.RateUiResponseDto;
import com.bank.frontui.service.ExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rates")
public class ExchangeController {
    @Autowired
    private ExchangeService exchangeService;

    @GetMapping
    Mono<List<RateUiResponseDto>> getRates(@AuthenticationPrincipal OidcUser oidcUser) {
        final var login = oidcUser.getPreferredUsername();
        log.info("Пользователь {} запрашивает курсы валют", login);

        return exchangeService.getRates(login)
                .doOnError(error -> log.error("Ошибка получения курсов для пользователя {}: {}", login, error.getMessage()));
    }

}
