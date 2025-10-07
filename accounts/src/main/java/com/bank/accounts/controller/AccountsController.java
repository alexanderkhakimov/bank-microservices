package com.bank.accounts.controller;


import com.bank.accounts.dto.*;
import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import com.bank.accounts.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegistrationDto dto) {
        accountService.creatUserAccount(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<UserAccountDto> getMyAccount(Authentication authentication) {
        final var account = accountService.getUserAccount(authentication);
        final var balances = accountService.getBalances(authentication).stream()
                .map(balance -> AccountBalanceDto.builder()
                        .currency(balance.getCurrency())
                        .balance(BigDecimal.valueOf(balance.getBalance()))
                        .isExists(balance.isExists())
                        .build())
                .toList();

        final var accountResponse = UserAccountDto.builder()
                .name(account.getName())
                .login(account.getLogin())
                .birthdate(account.getBirthdate())
                .email(account.getEmail())
                .balances(balances)
                .build();

        return ResponseEntity.ok(accountResponse);
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserAccount> getAccount(@PathVariable String login) {
        var userAccount = accountService.getUserAccountByLogin(login);
        return ResponseEntity.ok(userAccount);
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

        logger.info("UpdateRequest user request: {}", request);

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

    @GetMapping("/me/balances")
    public ResponseEntity<List<AccountBalance>> getBalances(Authentication authentication) {
        List<AccountBalance> balances = accountService.getBalances(authentication);
        return ResponseEntity.ok(balances);
    }
}

