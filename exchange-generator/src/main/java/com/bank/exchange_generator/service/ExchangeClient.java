package com.bank.exchange_generator.service;

import com.bank.exchange_generator.dto.UpdateRateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Service
public class ExchangeClient {
    @Value("${exchange.api-url}")
    private final String apiUrl = "http://localhost:8086";
    private final RestClient restClient;
    private final OAuth2Service oAuth2Service;

    public ExchangeClient(RestClient.Builder restClientBuilder, OAuth2Service oAuth2Service) {
        log.info("BaseUrl = {}", apiUrl);
        this.restClient = restClientBuilder
                .baseUrl(apiUrl)
                .build();
        this.oAuth2Service = oAuth2Service;
    }

    public void updateRates(List<UpdateRateRequestDto> rates) {
        try {
            restClient.post()
                    .uri("/")
                    .header("Authorization", "Bearer " + oAuth2Service.getTokenValue())
                    .body(rates)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new HttpClientErrorException(response.getStatusCode(), response.getStatusText());
                    })
                    .toBodilessEntity();
            log.info("Обновленные курсы успешно отправлены в {}", apiUrl);
        } catch (HttpClientErrorException e) {
            log.error("HTTP ошибка при отправке в  {}: status {}, response {}", apiUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Ошибка при отправке курсов: ", e);
        }

    }

}