package com.bank.frontui.service;

import com.bank.frontui.config.properties.ClientProperties;
import com.bank.frontui.dto.CashFormRequest;
import com.bank.frontui.dto.CashRequest;
import com.bank.frontui.exception.CashOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;

@Slf4j
@Service
public class CashFrontService {
    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public CashFrontService(WebClient.Builder builder, ClientProperties clientProperties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(clientProperties.getCashClient().getBaseUrl())
                .build();
    }

    public Mono<ResponseEntity<Void>> processCashOperation(CashFormRequest request, String login) {
        final var cashRequest = CashRequest.builder()
                .currency(request.currency())
                .value(BigDecimal.valueOf(request.value()))
                .action(request.toCashAction())
                .build();
        log.info("Запрос на обналичивание денежных средства сформирован {}", cashRequest);
        return oAuth2Service.getTokenValue()
                .flatMap(accessToken -> {
                    if (accessToken == null || accessToken.isEmpty()) {
                        log.warn("Недействительный токен для пользователя {}", login);
                        return Mono.error(new AuthenticationException("Не действительный токен"));
                    }
                    return webClient.post()
                            .uri("/user/{login}/cash", login)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError(), response -> {
                                log.warn("Клиентская ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error(new CashOperationException("Счет пользователя %s не найдет: " + login));
                            })
                            .onStatus(status -> status.is5xxServerError(), response -> {
                                log.error("Серверная ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error(new CashOperationException("Сервис снятия наличных недоступен"));
                            })
                            .toBodilessEntity()
                            .doOnError(error -> log.error("Ошибка при обналичивании денежных средств дли пользователя {}: {}", login, error.getMessage()));
                })
                .doOnSuccess(value -> log.info("Пользователь {} успешно обналичил денежные средства", login))
                .doOnError(error -> log.warn("Ошибка при выполнении операции обналичивании денежных средств {}", error.getMessage()));
    }
}
