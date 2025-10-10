package com.bank.accounts.service;

import com.bank.accounts.dto.AccountBalanceUpdateRequest;
import com.bank.accounts.dto.RegisterUserRequestDto;
import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import com.bank.accounts.repository.AccountBalanceRepository;
import com.bank.accounts.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

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

    public void creatUserAccount(RegisterUserRequestDto dto) {
        if (userAccountRepository.findByLogin(dto.login()).isPresent()) {
            throw new IllegalArgumentException("Логин уже существует");
        }
        final var newUserAccount = UserAccount.builder()
                .login(dto.login())
                .name(dto.name())
                .password(dto.password())
                .birthdate(dto.birthdate())
                .email(dto.email())
                .build();

        userAccountRepository.save(newUserAccount);
        //sendNotification("Новый аккаунт создан: " + login);
    }

    public UserAccount getUserAccount(Authentication authentication) {
        final var jwt = (Jwt) authentication.getPrincipal();
        final var login = jwt.getClaimAsString("preferred_username");

        return userAccountRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найдет или не существует!"));
    }

    @Transactional
    public UserAccount updateUserAccount(
            Authentication authentication,
            String login,
            List<AccountBalance> account,
            String name,
            LocalDate birthdate) {
        var user = getUserAccount(authentication);

        // Получаем текущую коллекцию балансов
        var balances = user.getBalances();
        if (balances == null) {
            balances = new ArrayList<>();
            user.setBalances(balances);
        }

        // Создаём карту для быстрого поиска существующих балансов по валюте
        var existingBalancesByCurrency = balances.stream()
                .collect(Collectors.toMap(
                        AccountBalance::getCurrency,
                        b -> b,
                        (existing, replacement) -> replacement // Сохраняем последнюю запись при дубликате
                ));

        // Обновляем или создаём балансы
        var newBalances = account.stream()
                .map(accountBalance -> {
                    var existing = existingBalancesByCurrency.getOrDefault(accountBalance.getCurrency(),
                            AccountBalance.builder()
                                    .userAccount(user)
                                    .currency(accountBalance.getCurrency())
                                    .build());
                    existing.setBalance(accountBalance.getBalance());
                    existing.setExists(accountBalance.isExists());
                    return existing;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        // Очищаем текущую коллекцию и добавляем обновлённые/новые балансы
        balances.clear();
        balances.addAll(newBalances);

        // Обновляем UserAccount
        user.setLogin(login);
        user.setName(name);
        user.setBirthdate(birthdate);

        var updatedAccount = userAccountRepository.save(user);
        logger.info("Счёт обновлен: id={}, login={}", updatedAccount.getId(), updatedAccount.getLogin());
        return updatedAccount;
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
        var account = getUserAccount(authentication);

        if (accountBalanceRepository.findByUserAccountAndCurrency(account, currency).isPresent()) {
            throw new IllegalArgumentException("Счёт в валюте " + currency + " уже существет!");
        }
        var balance = AccountBalance.builder()
                .balance(BigDecimal.valueOf(initBalance))
                .userAccount(account)
                .currency(currency)
                .build();
        accountBalanceRepository.save(balance);
        sendNotification("Счёт добавлен: " + currency + " для " + account.getLogin());
        return balance;
    }

    public List<AccountBalance> getBalances(UserAccount account) {
        return accountBalanceRepository.findAllByUserAccount(account);
    }

    public void deleteBalance(Authentication authentication, Currency currency) {
        var account = getUserAccount(authentication);
        AccountBalance balance = accountBalanceRepository.findByUserAccountAndCurrency(account, currency)
                .orElseThrow(() -> new RuntimeException("Счёт не найден"));
        if (balance.getBalance().doubleValue() != 0) {
            throw new IllegalStateException("Нельзя удалить счёт с ненулевым балансом");
        }
        accountBalanceRepository.delete(balance);
        sendNotification("Счёт удалён: " + currency + " для " + account.getLogin());
    }


    private void sendNotification(String message) {
        var notificationsUrl = "http://localhost:8083/api/notifications";  // Заглушка
        restTemplate.postForEntity(notificationsUrl, message, String.class);
    }

    @Transactional(readOnly = true)
    public UserAccount getUserAccountByLogin(String login) {
        return userAccountRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Пользователь с логином %s не найден!".formatted(login)));
    }

    @Transactional
    public void updateBalance(String login, AccountBalanceUpdateRequest request) {
        var userAccount = getUserAccountByLogin(login);
        var balance = userAccount.getBalances().stream()
                .filter(b -> b.getCurrency().toString().equals(request.currency()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Счёт с валютой %s не найден!".formatted(request.currency())));
        balance.setBalance(BigDecimal.valueOf(request.balance()));
        userAccountRepository.save(userAccount);
    }
}
