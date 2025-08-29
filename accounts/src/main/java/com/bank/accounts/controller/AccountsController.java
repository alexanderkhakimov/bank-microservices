package com.bank.accounts.controller;


import com.bank.accounts.dto.BalanceRequest;
import com.bank.accounts.dto.RegistrationRequest;
import com.bank.accounts.dto.UpdateRequest;
import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import com.bank.accounts.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountsController {
    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAccount> register(@Valid @RequestBody RegistrationRequest request) {
        var account = accountService.creatUserAccount(
                request.keycloakId(),
                request.login(),
                request.name(),
                request.email(),
                request.birthdate()
        );

        return ResponseEntity.ok(account);
    }

    @GetMapping("/me")
    public ResponseEntity<UserAccount> getMyAccount(Authentication authentication) {
        UserAccount account = accountService.getAccount(authentication);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/me")
    public ResponseEntity<UserAccount> updateMyAccount(@Valid @RequestBody UpdateRequest request, Authentication authentication) {
        UserAccount account = accountService.updateUserAccount(
                authentication,
                request.login(),
                request.name(),
                request.email(),
                request.birthdate()
        );
        return ResponseEntity.ok(account);
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

