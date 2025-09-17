package com.bank.frontui.service;

import com.bank.frontui.dto.CashFormRequest;
import com.bank.frontui.dto.CashRequest;
import com.bank.frontui.exception.CashOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CashFrontService {
    private static final Logger logger = LoggerFactory.getLogger(CashFrontService.class);
    private final WebClient webClient;
    private final String cashServiceUrl = "http://localhost:8084/user";

    public CashFrontService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> processCashOperation(String login, CashFormRequest request) {
        CashRequest cashRequest = CashRequest.builder()
                .currency(request.currency())
                .value(request.value())
                .action(request.toCashAction())
                .build();

        return webClient.post()
                .uri(cashServiceUrl + "/{login}/cash", login)
                .bodyValue(cashRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(List.class)
                                .map(body -> new CashOperationException("Ошибка от сервиса Cash: " + body)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.just(new CashOperationException("Сервис Cash недоступен")))
                .bodyToMono(List.class)
                .flatMap(errors -> {
                    if (!errors.isEmpty()) {
                        return Mono.error(new CashOperationException(String.join(", ", (List<String>) errors)));
                    }
                    return Mono.empty();
                });
    }
}
