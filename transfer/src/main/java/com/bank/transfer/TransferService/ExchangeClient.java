package com.bank.transfer.TransferService;

import com.bank.transfer.config.properties.ClientProperties;
import com.bank.transfer.dto.RateResponseDto;
import com.bank.transfer.dto.TransferRequest;
import com.bank.transfer.exception.HttpClientException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@Slf4j
public class ExchangeClient {
    private final RestClient restClient;

    public ExchangeClient(RestClient.Builder restClient, ClientProperties clientProperties) {
        this.restClient = restClient
                .baseUrl(clientProperties.getExchangeClient().getBaseurl())
                .build();
    }

    public BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        final var rates = getRates();
        final var from = getExchangeRate(rates, fromCurrency);
        final var to = getExchangeRate(rates, toCurrency);

        final var amountToRub = amount.multiply(from);
        final var result = amountToRub.divide(to, 6, RoundingMode.HALF_UP);
        log.debug("Конвернтация прошла {} {} to {} {} = {}",
                amount, fromCurrency, result, toCurrency, result);
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getExchangeRate(List<RateResponseDto> rates, String currency) {
        return rates.stream()
                .filter(rate -> rate.getCurrency().getTitle().equals(currency))
                .map(RateResponseDto::getValue)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Валюты {} не в списке обменного курса", currency);
                    return new RuntimeException();
                });
    }

    private List<RateResponseDto> getRates() {
        return restClient.get()
                .uri("/rates")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request1, response) -> {
                    throw new HttpClientException("Клиентская ошибка с кодом " + response.getStatusCode());
                }))
                .onStatus(HttpStatusCode::is5xxServerError, ((request1, response) ->
                {
                    throw new HttpClientException("Серверная ошибка с кодом " + response.getStatusCode());
                })).body(new ParameterizedTypeReference<List<RateResponseDto>>() {
                });
    }

}
