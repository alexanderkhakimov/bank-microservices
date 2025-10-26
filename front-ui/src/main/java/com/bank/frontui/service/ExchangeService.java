package com.bank.frontui.service;

import com.bank.frontui.config.properties.ClientProperties;
import com.bank.frontui.dto.ExchangeRate;
import com.bank.frontui.dto.RateUiResponseDto;
import com.bank.frontui.exception.UserNotFoundException;
import com.bank.frontui.exception.UserServiceException;
import com.bank.frontui.model.UserAccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.List;

@Service
@Slf4j
public class ExchangeService {
    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;
    private final ClientProperties clientProperties;

    public ExchangeService(WebClient.Builder builder, ClientProperties clientProperties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(clientProperties.getExchangeClient().getBaseUrl())
                .build();
        this.clientProperties = clientProperties;
    }
    public Mono<List<RateUiResponseDto>> getRates(String login) {
        return oAuth2Service.getTokenValue()
                .flatMap(accessToken -> {
                    if (accessToken == null || accessToken.isEmpty()) {
                        log.warn("Недействительный токен для пользователя {}", login);
                        return Mono.error(new AuthenticationException("Не действительный токен"));
                    }

                    return webClient.get()
                            .uri("/")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError(), response -> {
                                log.warn("Клиентская ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error(new UserNotFoundException("Курсы валют не найдены"));
                            })
                            .onStatus(status -> status.is5xxServerError(), response -> {
                                log.error("Серверная ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error(new UserServiceException("Сервис курсов валют недоступен"));
                            })
                            .bodyToFlux(RateUiResponseDto.class)
                            .collectList()
                            .doOnSuccess(rates -> log.debug("Получено {} курсов валют для пользователя {}", rates.size(), login))
                            .doOnError(error -> log.error("Ошибка WebClient для пользователя {}: {}", login, error.getMessage()));
                })
                .doOnSuccess(rates -> log.info("Успешно получены курсы валют для пользователя {}", login))
                .doOnError(error -> log.warn("Общая ошибка получения курсов для {}: {}", login, error.getMessage()));
    }
}
