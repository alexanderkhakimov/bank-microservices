package com.bank.frontui.service;

import com.bank.frontui.dto.RegistrationRequest;
import com.bank.frontui.dto.UpdatePasswordRequest;
import com.bank.frontui.model.UserAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Service
public class AccountClient {

    @Value("${accounts.api.url:http://localhost:8082/api/v1/accounts}")
    private String accountsApiUrl;

    private final WebClient webClient;


    public AccountClient(WebClient webClient) {
        this.webClient = webClient;
    }

    //
//    public UserAccount getAccount(OidcUser  authentication) {
//
//    }
//
    public UserAccount register(String keycloakId, String password, String login, String name, String email, LocalDate birthdate) {
        var request = RegistrationRequest.builder()
                .keycloakId(keycloakId)
                .password(password)
                .login(login)
                .name(name)
                .email(email)
                .birthdate(birthdate)
                .build();
        return webClient.post()
                .uri(accountsApiUrl + "/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserAccount.class)
                .block();
    }

    public UserAccount updatePassword(String login, String password) {
        var request = UpdatePasswordRequest.builder()
                .login(login)
                .password(password)
                .build();

        return webClient.post()
                .uri(accountsApiUrl + "/me/updatePassword")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserAccount.class)
                .block();
    }
//
//    public AccountBalance addBalance(OidcUser  authentication, Currency currency, double initialBalance) {
//        BalanceRequest request = BalanceRequest.builder()
//                .currency(currency)
//                .initialBalance(initialBalance)
//                .build();
//        return webClient.post()
//    }
//
//    public void deleteBalance(OidcUser  authentication, Currency currency) {
//
//    }
//
//    public List<AccountBalance> getBalances(OidcUser  authentication) {
//
//    }
//
//    public List<UserAccount> getUsers(OidcUser  authentication) {
//
//    }


}