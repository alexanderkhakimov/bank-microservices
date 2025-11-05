package com.bank.accounts.service;

import com.bank.accounts.dto.AccountBalanceDto;
import com.bank.accounts.dto.AccountBalanceUpdateRequest;
import com.bank.accounts.dto.RegisterUserRequestDto;
import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import com.bank.accounts.repository.AccountBalanceRepository;
import com.bank.accounts.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AccountService accountService;

    @Test
    void creatUserAccount_ShouldCreateUser_WhenLoginNotExists() {
        final var dto = new RegisterUserRequestDto(
                "newuser", "password", "New User",
                "new@email.com", LocalDate.of(1990, 1, 1)
        );

        when(userAccountRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> accountService.creatUserAccount(dto));

        verify(userAccountRepository).save(any(UserAccount.class));
        verify(userAccountRepository).save(argThat(user ->
                user.getLogin().equals("newuser") &&
                        user.getPassword().equals("password")
        ));
    }

    @Test
    void creatUserAccount_ShouldThrowException_WhenLoginExists() {
        final var dto = new RegisterUserRequestDto(
                "existinguser", "password", "Existing User",
                "existing@email.com", LocalDate.of(1990, 1, 1)
        );

        final var existingUser = UserAccount.builder().login("existinguser").build();
        when(userAccountRepository.findByLogin("existinguser")).thenReturn(Optional.of(existingUser));

        final var exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.creatUserAccount(dto));
        assertEquals("Логин уже существует", exception.getMessage());
    }

    @Test
    void getUserAccount_ShouldReturnUser_WhenAuthenticationValid() {
        final var login = "testuser";
        final var mockUser = UserAccount.builder()
                .id(1L)
                .login(login)
                .name("Test User")
                .email("test@email.com")
                .password("encodedPassword")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(login);
        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));

        final var result = accountService.getUserAccount(authentication);

        assertNotNull(result);
        assertEquals(login, result.getLogin());
        assertEquals("Test User", result.getName());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    void getUserAccount_ShouldThrowException_WhenUserNotFound() {
        final var login = "nonexistent";
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(login);
        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getUserAccount(authentication));
        assertEquals("Аккаунт не найдет или не существует!", exception.getMessage());
    }

    @Test
    void addBalance_ShouldThrowException_WhenCurrencyExists() {
        final var login = "testuser";
        final var mockUser = UserAccount.builder().id(1L).login(login).build();
        final var currency = Currency.USD;
        final var existingBalance = AccountBalance.builder()
                .id(1L)
                .currency(currency)
                .balance(BigDecimal.valueOf(500))
                .userAccount(mockUser)
                .isExists(true)
                .build();

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(login);
        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));
        when(accountBalanceRepository.findByUserAccountAndCurrency(mockUser, currency))
                .thenReturn(Optional.of(existingBalance));

        final var exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.addBalance(authentication, currency, 1000.0));
        assertEquals("Счёт в валюте " + currency + " уже существет!", exception.getMessage());
    }

    @Test
    void getBalances_ShouldReturnOnlyExistingBalances() {
        final var account = UserAccount.builder().id(1L).login("testuser").build();
        final var balances = List.of(
                AccountBalance.builder()
                        .id(1L)
                        .currency(Currency.USD)
                        .balance(BigDecimal.valueOf(1000))
                        .isExists(true)
                        .userAccount(account)
                        .build(),
                AccountBalance.builder()
                        .id(2L)
                        .currency(Currency.EUR)
                        .balance(BigDecimal.valueOf(500))
                        .isExists(false) // Этот баланс не должен вернуться
                        .userAccount(account)
                        .build(),
                AccountBalance.builder()
                        .id(3L)
                        .currency(Currency.RUB)
                        .balance(BigDecimal.valueOf(750))
                        .isExists(true)
                        .userAccount(account)
                        .build()
        );

        when(accountBalanceRepository.findAllByUserAccount(account)).thenReturn(balances);

        final var result = accountService.getBalances(account);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(AccountBalanceDto::isExists));
        assertTrue(result.stream().anyMatch(dto -> dto.currency() == Currency.USD));
        assertTrue(result.stream().anyMatch(dto -> dto.currency() == Currency.RUB));
        assertTrue(result.stream().noneMatch(dto -> dto.currency() == Currency.EUR));
    }

    @Test
    void deleteBalance_ShouldDelete_WhenBalanceIsZero() {
        final var login = "testuser";
        final var mockUser = UserAccount.builder().id(1L).login(login).build();
        final var currency = Currency.USD;
        final var zeroBalance = AccountBalance.builder()
                .id(1L)
                .currency(currency)
                .balance(BigDecimal.ZERO)
                .isExists(true)
                .userAccount(mockUser)
                .build();

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(login);
        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));
        when(accountBalanceRepository.findByUserAccountAndCurrency(mockUser, currency))
                .thenReturn(Optional.of(zeroBalance));

        assertDoesNotThrow(() -> accountService.deleteBalance(authentication, currency));

        verify(accountBalanceRepository).delete(zeroBalance);
    }

    @Test
    void deleteBalance_ShouldThrowException_WhenBalanceNotZero() {
        final var login = "testuser";
        final var mockUser = UserAccount.builder().id(1L).login(login).build();
        final var currency = Currency.USD;
        final var nonZeroBalance = AccountBalance.builder()
                .id(1L)
                .currency(currency)
                .balance(BigDecimal.valueOf(100))
                .isExists(true)
                .userAccount(mockUser)
                .build();

        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(login);
        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));
        when(accountBalanceRepository.findByUserAccountAndCurrency(mockUser, currency))
                .thenReturn(Optional.of(nonZeroBalance));

        final var exception = assertThrows(IllegalStateException.class,
                () -> accountService.deleteBalance(authentication, currency));
        assertEquals("Нельзя удалить счёт с ненулевым балансом", exception.getMessage());
    }

    @Test
    void updateBalance_ShouldUpdateBalance_WhenCurrencyExists() {
        final var login = "testuser";
        final var currency = Currency.USD;
        final var newBalance = BigDecimal.valueOf(2000);
        final var request = new AccountBalanceUpdateRequest(currency, newBalance);

        final var mockUser = UserAccount.builder().id(1L).login(login).build();
        final var existingBalance = AccountBalance.builder()
                .id(1L)
                .currency(currency)
                .balance(BigDecimal.valueOf(1000))
                .isExists(true)
                .userAccount(mockUser)
                .build();
        mockUser.setBalances(List.of(existingBalance));

        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));
        when(userAccountRepository.save(mockUser)).thenReturn(mockUser);

        accountService.updateBalance(login, request);

        assertEquals(newBalance, existingBalance.getBalance());
        verify(userAccountRepository).save(mockUser);
    }

    @Test
    void updatePassword_ShouldEncodePassword_WhenUserExists() {
        final var login = "testuser";
        final var rawPassword = "newpassword";
        final var encodedPassword = "encodedNewPassword";

        final var mockUser = UserAccount.builder()
                .id(1L)
                .login(login)
                .password("oldPassword")
                .build();

        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userAccountRepository.save(mockUser)).thenReturn(mockUser);

        accountService.updatePassword(login, rawPassword);

        verify(passwordEncoder).encode(rawPassword);
        verify(userAccountRepository).save(argThat(user ->
                user.getPassword().equals(encodedPassword)
        ));
    }

    @Test
    void updatePassword_ShouldThrowException_WhenUserNotFound() {
        final var login = "nonexistent";
        final var password = "newpassword";

        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.empty());

        final var exception = assertThrows(RuntimeException.class,
                () -> accountService.updatePassword(login, password));
        assertEquals("Аккаунт с логином " + login + " не найден!", exception.getMessage());
    }

    @Test
    void getUserAccountByLogin_ShouldReturnUser_WhenLoginExists() {
        final var login = "testuser";
        final var mockUser = UserAccount.builder()
                .id(1L)
                .login(login)
                .name("Test User")
                .build();

        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.of(mockUser));

        final var result = accountService.getUserAccountByLogin(login);

        assertNotNull(result);
        assertEquals(login, result.getLogin());
        assertEquals("Test User", result.getName());
    }

    @Test
    void getUserAccountByLogin_ShouldThrowException_WhenLoginNotFound() {
        final var login = "nonexistent";

        when(userAccountRepository.findByLogin(login)).thenReturn(Optional.empty());

        final var exception = assertThrows(RuntimeException.class,
                () -> accountService.getUserAccountByLogin(login));
        assertEquals("Пользователь с логином " + login + " не найден!", exception.getMessage());
    }
}