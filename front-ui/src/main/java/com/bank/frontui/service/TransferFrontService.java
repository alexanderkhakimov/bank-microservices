//package com.bank.frontui.service;
//
//import com.bank.frontui.dto.TransferRequest;
//import com.bank.frontui.exception.CashOperationException;
//import com.bank.frontui.exception.TransferOperationException;
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Service
//public class TransferFrontService {
//    private static final Logger logger = LoggerFactory.getLogger(TransferFrontService.class);
//    private final WebClient webClient;
//    private final String transferServiceUrl = "http://localhost:8085";
//
//    public TransferFrontService(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    public Mono<Void> processTransfer(@Valid TransferRequest request, String login) {
//        return webClient.post()
//                .uri(transferServiceUrl + "/{login}/transfer", login)
//                .bodyValue(request)
//                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError,
//                        clientResponse -> Mono.just(new TransferOperationException("Ошибка при запросе в сервис в Transfer")))
//                .onStatus(HttpStatusCode::is5xxServerError,
//                        clientResponse -> Mono.just(new TransferOperationException("Ошибка в сервисе Transfer")))
//                .bodyToMono(Void.class)
//                .onErrorMap(Throwable.class, error ->
//                        new TransferOperationException("Ошибка соединения: " + error.getMessage()));
//    }
//}
