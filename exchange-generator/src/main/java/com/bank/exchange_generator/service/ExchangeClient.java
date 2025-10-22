package com.bank.exchange_generator.service;

import com.bank.exchange_generator.config.properties.ClientProperties;
import com.bank.exchange_generator.dto.UpdateRateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.List;

@Slf4j
@Service
public class ExchangeClient {
    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;
    private final ClientProperties clientProperties;

    public ExchangeClient(WebClient.Builder webClientBuilder, OAuth2Service oAuth2Service, ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
        log.info("BaseUrl = {}", clientProperties.getExchangeClient().getBaseUrl());
        this.webClient = webClientBuilder
                .baseUrl(clientProperties.getExchangeClient().getBaseUrl())
                .build();
        this.oAuth2Service = oAuth2Service;
    }

    public void updateRates(List<UpdateRateRequestDto> rates) {
        oAuth2Service.getTokenValue()
                .flatMap(accessToken -> {
                    if (accessToken == null || accessToken.isEmpty()) {
                        log.warn("Недействительный токен для сервиса");
                        return Mono.error(new AuthenticationException("Не действительный токен"));
                    }
                    log.info("Запрос с телом {}", rates.toString());
                    return webClient.post()
                            .uri("/")
                            .bodyValue(rates)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError(), response ->
                                    Mono.error(new RuntimeException("Клиентская ошибка со статусом: " + response.statusCode()))
                            )
                            .onStatus(status -> status.is5xxServerError(), response ->
                                    Mono.error(new RuntimeException("Серверная ошибка со статусом: " + response.statusCode()))
                            ).bodyToMono(Void.class);
                })
                .doOnSuccess(user -> log.info("Успешно направлен запрос пользователя {}", rates))
                .doOnError(error -> log.warn("Ошибка при отправки запроса {}", error.getMessage()))
                .subscribe();

    }

}