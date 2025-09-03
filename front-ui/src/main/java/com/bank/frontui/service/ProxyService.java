package com.bank.frontui.service;

import com.bank.frontui.dto.ExchangeRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ProxyService {
    @Value("${exchange.api.url:http://localhost:8083/api}")
    private String exchangeApiUrl;
    private final WebClient webClient;

    public ProxyService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<ExchangeRate> getRates() {
        return webClient.get()
                .uri(exchangeApiUrl + "/rates")
                .retrieve()
                .bodyToFlux(ExchangeRate.class)
                .collectList()
                .block();
    }
}
