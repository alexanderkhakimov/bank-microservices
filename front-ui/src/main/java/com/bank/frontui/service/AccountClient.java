package com.bank.frontui.service;

import com.bank.frontui.config.properties.ClientProperties;
import com.bank.frontui.dto.RegisterUserRequestDto;
import com.bank.frontui.exception.UserNotFoundException;
import com.bank.frontui.exception.UserServiceException;
import com.bank.frontui.model.UserAccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;

@Slf4j
@Service
public class AccountClient {

    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;
    private final ClientProperties clientProperties;

    public AccountClient(WebClient.Builder builder, ClientProperties clientProperties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(clientProperties.getUserClient().getBaseUrl())
                .build();
        this.clientProperties = clientProperties;
    }


    public Mono<UserAccountDto> getAccount(String login) {
        return oAuth2Service.getTokenValue()
                .flatMap(accessToken -> {
                    if (accessToken == null || accessToken.isEmpty()) {
                        log.warn("Недействительный токен для пользователя {}", login);
                        return Mono.error(new AuthenticationException("Не действительный токен"));
                    }
                    return webClient.get()
                            .uri("/{login}", login)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError(), response -> {
                                log.warn("Клиентская ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error(new UserNotFoundException("Пользователь не найден: " + login));
                            })
                            .onStatus(status -> status.is5xxServerError(), response -> {
                                log.error("Серверная ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error(new UserServiceException("Сервис пользователей оступен"));
                            })
                            .bodyToMono(UserAccountDto.class)
                            .doOnError(error -> log.error("Ошибка получения профиля пользователя {}: {}", login, error.getMessage()));
                })
                .doOnSuccess(user -> log.info("Успешно получен акаунт пользователя {}. {}", login, user))
                .doOnError(error -> log.warn("Ошибка при выполнении операции олучения профиля {}", error.getMessage()));
    }

    public Mono<Void> register(RegisterUserRequestDto requestDto) {
        return oAuth2Service.getTokenValue()
                .flatMap(accessToken -> {
                    if (accessToken == null || accessToken.isEmpty()) {
                        log.warn("Недействительный токен для пользователя {}", requestDto.login());
                        return Mono.error(new AuthenticationException("Не действительный токен"));
                    }
                    return webClient.post()
                            .uri("/register")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .bodyValue(requestDto)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError(), response -> {
                                log.warn("Клиентская ошибка для пользователя {}: статус {}", requestDto.login(), response.statusCode());
                                return Mono.error(new UserNotFoundException("Пользователь не найден: " + requestDto.login()));
                            })
                            .onStatus(status -> status.is5xxServerError(), response -> {
                                log.error("Серверная ошибка для пользователя {}: статус {}", requestDto.login(), response.statusCode());
                                return Mono.error(new UserServiceException("Сервис пользователей недоступен"));
                            })
                            .toBodilessEntity();
                })
                .doOnSuccess(response -> log.info("Успешно зарегистрирован пользователь {}, ответ {}", requestDto.login(), response))
                .doOnError(error -> log.error("Ошибка регистрации {}: {}", requestDto.login(), error.getMessage()))
                .then();

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