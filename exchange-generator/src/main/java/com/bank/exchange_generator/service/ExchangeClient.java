package com.bank.exchange_generator.service;

import com.bank.exchange_generator.dto.UpdateRateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
public class ExchangeClient {
    private final RestClient restClient;

    public ExchangeClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void updateRates(List<UpdateRateRequestDto> rates) {
        try {
            restClient.post()
                    .uri("/")
                    .body(rates)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Не удалось отправть курсы валют. Ошибка: ", e);
        }
    }
}
