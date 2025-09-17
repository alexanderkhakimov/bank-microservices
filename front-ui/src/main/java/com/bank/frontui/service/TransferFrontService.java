package com.bank.frontui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TransferFrontService {
    private static final Logger logger = LoggerFactory.getLogger(TransferFrontService.class);
    private final WebClient webClient;
    private final String transferServiceUrl = "http://localhost:8085/getInfo";

    public TransferFrontService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getInfo() {
        logger.info("Запрос принят в сервисе");
        return webClient.get()
                .uri(transferServiceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
