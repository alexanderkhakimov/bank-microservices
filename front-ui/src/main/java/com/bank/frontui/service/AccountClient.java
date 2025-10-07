package com.bank.frontui.service;

import com.bank.frontui.config.properties.ClientProperties;
import com.bank.frontui.dto.UserRegistrationRequest;
import com.bank.frontui.model.UserAccountDto;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AccountClient {

    private final WebClient webClient;
    private final ClientProperties clientProperties;

    public AccountClient(WebClient webClient, ClientProperties clientProperties) {
        this.webClient = webClient;
        this.clientProperties = clientProperties;
    }


    public Mono<UserAccountDto> getAccount(OidcUser authentication) {
        return webClient.get()
                .uri(clientProperties.getUserClient().getBaseUrl())
                .retrieve()
                .bodyToMono(UserAccountDto.class);
    }

    public Mono<Void> register(UserRegistrationRequest request) {
        return webClient.post()
                .uri(clientProperties.getUserClient().getBaseUrl() + "/register")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity().then();
    }
//
//    public void updatePassword(String login, String password) {
//        var request = UpdatePasswordRequest.builder()
//                .login(login)
//                .password(password)
//                .build();
//
//        webClient.post()
//                .uri(accountsApiUrl + "/me/updatePassword")
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(UserAccountDto.class)
//                .block();
//    }
//
//    public Mono<Void> updateAccount(String login, List<AccountBalanceDto> newBalance, String name, LocalDate birthdate) {
//
//        var request = UpdateRequest.builder()
//                .login(login)
//                .name(name)
//                .account(newBalance)
//                .birthdate(birthdate)
//                .build();
//
//        return webClient.put()
//                .uri(accountsApiUrl + "/me/updateAccount")
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(Void.class);
//    }
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