package com.bank.frontui.controller;

import com.bank.frontui.model.AccountBalanceDto;
import com.bank.frontui.model.Currency;
import com.bank.frontui.model.UserAccountDto;
import com.bank.frontui.service.AccountClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class MainController {

    private final AccountClient accountClient;

    public MainController(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @GetMapping("/test")
    public Mono<String> testPage(Model model) {
        model.addAttribute("message", "Тестовое сообщение");
        model.addAttribute("timestamp", LocalDateTime.now());
        return Mono.just("test-template");
    }

    @GetMapping("/")
    public String getTest(@AuthenticationPrincipal OidcUser oidcUser, Model model) {

        var login = oidcUser.getPreferredUsername();
        var email = oidcUser.getEmail();
        var name = oidcUser.getFullName();
        log.info("Пользователь {} загружает основную страницу в тестовом блоке", login);

        model.addAttribute("login", login);
        model.addAttribute("currency", Arrays.asList(Currency.values()));
        var defaultBalances = createDefaultBalances();
        model.addAttribute("accounts", defaultBalances);

        return "main";
    }

    @GetMapping("/main")
    public Mono<String> mainPage(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        String login = oidcUser.getPreferredUsername();
        log.info("Пользователь {} загружает основную страницу", login);

        model.addAttribute("login", login);
        model.addAttribute("currency", Arrays.asList(Currency.values()));
        var defaultBalances = createDefaultBalances();
        model.addAttribute("accounts", defaultBalances);

        return accountClient.getAccount(oidcUser)
                .doOnNext(account -> log.info("Данные аккаунта загружены: {}", account))
                .flatMap(account -> processAccountData(account, model, login))
                .onErrorResume(e -> handleAccountError(e, model, login))
                .thenReturn("main");
    }

    private Mono<String> processAccountData(UserAccountDto account, Model model, String login) {
        model.addAttribute("name", account.name());
        model.addAttribute("birthdate", account.birthdate());

        List<AccountBalanceDto> balances = createBalances(account);
        model.addAttribute("accounts", balances);

        if (balances.stream().noneMatch(AccountBalanceDto::isExists)) {
            log.warn("No active accounts found for user: {}", login);
            model.addAttribute("accountsMessage", "Счета отсутствуют. Выберите валюту и сохраните, чтобы создать счёт.");
        }

        return Mono.just("main");
    }

    private Mono<String> handleAccountError(Throwable e, Model model, String login) {
        log.error("Ошибка при загрузке аккаунта {}: {}", login, e.getMessage());
        model.addAttribute("userAccountsError", "Не удалось загрузить данные аккаунта: " + e.getMessage());

        List<AccountBalanceDto> defaultBalances = createDefaultBalances();
        model.addAttribute("accounts", defaultBalances);
        model.addAttribute("accountsMessage", "Ошибка загрузки счетов. Выберите валюту и сохраните.");

        return Mono.just("main");
    }

    private List<AccountBalanceDto> createBalances(UserAccountDto account) {
        return Arrays.stream(Currency.values())
                .map(currency -> account.balances().stream()
                        .filter(b -> b.currency() == currency)
                        .findFirst()
                        .orElse(createDefaultBalance(currency)))
                .collect(Collectors.toList());
    }

    private List<AccountBalanceDto> createDefaultBalances() {
        return Arrays.stream(Currency.values())
                .map(this::createDefaultBalance)
                .collect(Collectors.toList());
    }

    private AccountBalanceDto createDefaultBalance(Currency currency) {
        return AccountBalanceDto.builder()
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .isExists(false)
                .build();
    }

}
