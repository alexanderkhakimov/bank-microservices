package com.bank.frontui.service;

import com.bank.frontui.config.properties.ClientProperties;
import com.bank.frontui.dto.TransferRequest;
import com.bank.frontui.exception.CashOperationException;
import com.bank.frontui.exception.TransferOperationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.util.List;
@Slf4j
@Service
public class TransferFrontService {
    private final WebClient webClient;
    private final OAuth2Service oAuth2Service;

    public TransferFrontService(WebClient.Builder builder, ClientProperties clientProperties, OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
        this.webClient = builder
                .baseUrl(clientProperties.getTransferClient().getBaseUrl())
                .build();
    }

    public Mono<ResponseEntity<Void>> processTransfer(@Valid TransferRequest request, String login) {
        log.info("Запрос на перевод денежных стредств сформирован{}", request);
        return oAuth2Service.getTokenValue()
                .flatMap(accessToken -> {
                    if (accessToken == null || accessToken.isEmpty()) {
                        log.warn("Недействительный токен для пользователя {}", login);
                        return Mono.error(new AuthenticationException("Не действительный токен"));
                    }
                    return webClient.post()
                            .uri("/user/{login}/transfer", login)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .bodyValue(request)
                            .retrieve()
                            .onStatus(status -> status.is4xxClientError(), response -> {
                                log.warn("Клиентская ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error((new TransferOperationException("Ошибка при запросе в сервис в Transfer")));
                            })
                            .onStatus(status -> status.is5xxServerError(), response -> {
                                log.error("Серверная ошибка для пользователя {}: статус {}", login, response.statusCode());
                                return Mono.error(new TransferOperationException("Сервисе Transfer недоступен"));
                            })
                            .toBodilessEntity()
                            .doOnError(error -> log.error("Ошибка при переводе денежных средств пользователю {}: {}", request.toLogin(), error.getMessage()));
                })
                .doOnSuccess(value -> log.info("Пользователь {} успешно первел денежные средства", login))
                .doOnError(error -> log.warn("Ошибка при выполнении операции превод денежных средств {}", error.getMessage()));
    }
}
