package com.bank.accounts.service;

import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import com.bank.accounts.repository.AccountBalanceRepository;
import com.bank.accounts.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class AccountService {
    private final AccountBalanceRepository accountBalanceRepository;
    private final UserAccountRepository userAccountRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountBalanceRepository accountBalanceRepository, UserAccountRepository userAccountRepository, RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.userAccountRepository = userAccountRepository;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccount creatUserAccount(
            String keyClockId,
            String password,
            String login,
            String name,
            String email,
            LocalDate birthdate) {
        if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("Возраст должен быть старше 18 лет");
        }

        if (userAccountRepository.findByLogin(login).isPresent()) {
            throw new IllegalArgumentException("Логин уже существует");
        }

        var newUserAccount = UserAccount.builder()
                .login(login)
                .name(name.toLowerCase())
                .keyClockId(keyClockId)
                .password(password)
                .birthdate(birthdate)
                .email(email)
                .build();

        var saved = userAccountRepository.save(newUserAccount);
        //sendNotification("Новый аккаунт создан: " + login);
        return saved;
    }

    public UserAccount getAccount(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var keyClockId = jwt.getSubject();

        return userAccountRepository.findByKeyClockId(keyClockId)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найдет или не существует!"));
    }

    public UserAccount updateUserAccount(
            Authentication authentication,
            String login,
            String name,
            String email,
            LocalDate birthdate) {
        var exitingAccount = getAccount(authentication);

        var newAccount = UserAccount.builder()
                .id(exitingAccount.getId())
                .keyClockId(exitingAccount.getKeyClockId())
                .name(name)
                .login(login)
                .email(email)
                .balances(exitingAccount.getBalances())
                .birthdate(birthdate)
                .build();
        userAccountRepository.save(newAccount);
        sendNotification("Аккаунт обновлён: " + newAccount.getLogin());
        return newAccount;
    }

    public void updatePassword(
            String login,
            String password
    ) {

        var account = userAccountRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Аккаунт с логином %s не найден!".formatted(login)));
        account.setPassword(passwordEncoder.encode(password));
        userAccountRepository.save(account);
    }


    public AccountBalance addBalance(
            Authentication authentication,
            Currency currency,
            double initBalance) {
        var account = getAccount(authentication);

        if (accountBalanceRepository.findByUserAccountAndCurrency(account, currency).isPresent()) {
            throw new IllegalArgumentException("Счёт в валюте " + currency + " уже существет!");
        }
        var balance = AccountBalance.builder()
                .balance(initBalance)
                .userAccount(account)
                .currency(currency)
                .build();
        accountBalanceRepository.save(balance);
        sendNotification("Счёт добавлен: " + currency + " для " + account.getLogin());
        return balance;
    }

    public List<AccountBalance> getBalances(Authentication authentication) {
        UserAccount account = getAccount(authentication);
        return accountBalanceRepository.findAllByUserAccount(account);
    }

    public void deleteBalance(Authentication authentication, Currency currency) {
        var account = getAccount(authentication);
        AccountBalance balance = accountBalanceRepository.findByUserAccountAndCurrency(account, currency)
                .orElseThrow(() -> new RuntimeException("Счёт не найден"));
        if (balance.getBalance() != 0) {
            throw new IllegalStateException("Нельзя удалить счёт с ненулевым балансом");
        }
        accountBalanceRepository.delete(balance);
        sendNotification("Счёт удалён: " + currency + " для " + account.getLogin());
    }


    private void sendNotification(String message) {
        var notificationsUrl = "http://localhost:8083/api/notifications";  // Заглушка
        restTemplate.postForEntity(notificationsUrl, message, String.class);
    }

}
