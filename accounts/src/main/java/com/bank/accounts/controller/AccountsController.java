package com.bank.accounts.controller;


import com.bank.accounts.dto.*;
import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import com.bank.accounts.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountsController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserRequestDto dto) {
        log.info("Пользователь {} направил запрос на регистрацию", dto.login());
        accountService.creatUserAccount(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserAccountDto> getAccount(@PathVariable String login) {
        final var account = accountService.getUserAccountByLogin(login);
        final var balances = accountService.getBalances(account);
        log.info("Итоговые балансы для ответа: {}", balances);
        final var accountResponse = UserAccountDto.builder()
                .name(account.getName())
                .login(account.getLogin())
                .birthdate(account.getBirthdate())
                .email(account.getEmail())
                .balances(balances)
                .build();
        log.info("Итоговый акаунт для ответа: {}", accountResponse);
        return ResponseEntity.ok(accountResponse);
    }

    @PutMapping("/{login}/updateBalance")
    public ResponseEntity<Void> updateBalance(
            @PathVariable String login,
            @RequestBody AccountBalanceUpdateRequest request) {
        accountService.updateBalance(login, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/updateAccount")
    public ResponseEntity<UserAccount> updateMyAccount(@Valid @RequestBody UpdateRequest request, Authentication authentication) {

        log.info("UpdateRequest user request: {}", request);

        var account = accountService.updateUserAccount(
                authentication,
                request.login(),
                request.account(),
                request.name(),
                request.birthdate()
        );
        return ResponseEntity.ok(account);
    }

    @PostMapping("/me/updatePassword")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordRequest request) {
        accountService.updatePassword(request.login(), request.password());
        return ResponseEntity.ok().build();

    }

    @PostMapping("/me/balances")
    public ResponseEntity<AccountBalance> addBalance(@RequestBody BalanceRequest request, Authentication authentication) {
        AccountBalance balance = accountService.addBalance(authentication, request.currency(), request.initialBalance());
        return ResponseEntity.ok(balance);
    }

    @DeleteMapping("/me/balances/{currency}")
    public ResponseEntity<Void> deleteBalance(@PathVariable Currency currency, Authentication authentication) {
        accountService.deleteBalance(authentication, currency);
        return ResponseEntity.noContent().build();
    }
}

