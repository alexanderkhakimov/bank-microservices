package com.bank.accounts.controller;

import com.bank.accounts.dto.*;
import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import com.bank.accounts.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountsController accountsController;

    @Test
    void register_ShouldReturnOk_WhenValidRequest() {
        RegisterUserRequestDto request = new RegisterUserRequestDto(
                "testuser", "password", "Test User",
                "test@email.com", LocalDate.of(1990, 1, 1)
        );
        final var response = accountsController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(accountService).createUserAccount(request);
    }

    @Test
    void getAccount_ShouldReturnUserAccountDto_WhenLoginExists() {
        final var login = "testuser";
        final var mockAccount = UserAccount.builder()
                .id(1L)
                .login(login)
                .name("Test User")
                .email("test@email.com")
                .birthdate(LocalDate.of(1990, 1, 1))
                .build();

        final var mockBalances = List.of(
                new AccountBalanceDto(Currency.USD, BigDecimal.valueOf(1000), true)
        );

        when(accountService.getUserAccountByLogin(login)).thenReturn(mockAccount);
        when(accountService.getBalances(mockAccount)).thenReturn(mockBalances);

        final var response = accountsController.getAccount(login);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(login, response.getBody().login());
        assertEquals("Test User", response.getBody().name());
        assertEquals("test@email.com", response.getBody().email());
        assertEquals(mockBalances, response.getBody().balances());
    }

    @Test
    void updateBalance_ShouldReturnOk_WhenValidRequest() {
        final var login = "testuser";
        final var request = new AccountBalanceUpdateRequest(
                Currency.USD, BigDecimal.valueOf(1500)
        );

        final var response = accountsController.updateBalance(login, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(accountService).updateBalance(login, request);
    }

    @Test
    void updateMyAccount_ShouldReturnUpdatedAccount_WhenValidRequest() {
        final var authentication = mock(Authentication.class);

        final var accountBalance = AccountBalance.builder()
                .currency(Currency.USD)
                .balance(BigDecimal.valueOf(1000))
                .isExists(true)
                .build();

        final var request = UpdateRequest.builder()
                .name("New Name")
                .account(List.of(accountBalance))
                .login("newlogin")
                .birthdate(LocalDate.of(1995, 5, 5))
                .build();
        final var mockUpdatedAccount = UserAccount.builder()
                .id(1L)
                .login("newlogin")
                .name("New Name")
                .birthdate(LocalDate.of(1995, 5, 5))
                .build();

        when(accountService.updateUserAccount(
                authentication, "newlogin", request.account(), "New Name", request.birthdate()
        )).thenReturn(mockUpdatedAccount);

        final var response = accountsController.updateMyAccount(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(mockUpdatedAccount, response.getBody());
    }

    @Test
    void updatePassword_ShouldReturnOk_WhenValidRequest() {
        final var request = new UpdatePasswordRequest("testuser", "newpassword");

        final var response = accountsController.updatePassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(accountService).updatePassword("testuser", "newpassword");
    }

    @Test
    void addBalance_ShouldReturnCreatedBalance_WhenValidRequest() {
        final var authentication = mock(Authentication.class);
        final var request = new BalanceRequest(Currency.EUR, 500.0);

        final var mockBalance = AccountBalance.builder()
                .id(1L)
                .currency(Currency.EUR)
                .balance(BigDecimal.valueOf(500))
                .isExists(true)
                .build();

        when(accountService.addBalance(authentication, Currency.EUR, 500.0))
                .thenReturn(mockBalance);

        final var response = accountsController.addBalance(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(mockBalance, response.getBody());
    }

    @Test
    void deleteBalance_ShouldReturnNoContent_WhenBalanceDeleted() {
        Authentication authentication = mock(Authentication.class);
        Currency currency = Currency.USD;

        final var response = accountsController.deleteBalance(currency, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(accountService).deleteBalance(authentication, currency);
    }
}