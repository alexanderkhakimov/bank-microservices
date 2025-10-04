//package com.bank.frontui.service;
//
//import com.bank.frontui.dto.RegistrationRequest;
//import com.bank.frontui.dto.UpdatePasswordRequest;
//import com.bank.frontui.dto.UpdateRequest;
//import com.bank.frontui.model.AccountBalance;
//import com.bank.frontui.model.UserAccount;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//public class AccountClient {
//
//    @Value("${accounts.api.url:http://localhost:8082/api/v1/accounts}")
//    private String accountsApiUrl;
//
//    private final WebClient webClient;
//
//
//    public AccountClient(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//
//    public Mono<UserAccount> getAccount(OidcUser authentication) {
//        return webClient.get()
//                .uri(accountsApiUrl + "/me")
//                .retrieve()
//                .bodyToMono(UserAccount.class);
//    }
//
//    public UserAccount register(String keycloakId, String password, String login, String name, String email, LocalDate birthdate) {
//        var request = RegistrationRequest.builder()
//                .keycloakId(keycloakId)
//                .password(password)
//                .login(login)
//                .name(name)
//                .email(email)
//                .birthdate(birthdate)
//                .build();
//        return webClient.post()
//                .uri(accountsApiUrl + "/register")
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(UserAccount.class)
//                .block();
//    }
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
//                .bodyToMono(UserAccount.class)
//                .block();
//    }
//
//    public Mono<Void> updateAccount(String login, List<AccountBalance> newBalance, String name, LocalDate birthdate) {
//
//        var request = UpdateRequest.builder()
//                .login(login)
//                .name(name)
//                .account(newBalance)
//                .birthdate(birthdate)
//                .build();
//
//       return webClient.put()
//                .uri(accountsApiUrl + "/me/updateAccount")
//                .bodyValue(request)
//                .retrieve()
//                .bodyToMono(Void.class);
//    }
////
////    public AccountBalance addBalance(OidcUser  authentication, Currency currency, double initialBalance) {
////        BalanceRequest request = BalanceRequest.builder()
////                .currency(currency)
////                .initialBalance(initialBalance)
////                .build();
////        return webClient.post()
////    }
////
////    public void deleteBalance(OidcUser  authentication, Currency currency) {
////
////    }
////
////    public List<AccountBalance> getBalances(OidcUser  authentication) {
////
////    }
////
////    public List<UserAccount> getUsers(OidcUser  authentication) {
////
////    }
//
//
//}