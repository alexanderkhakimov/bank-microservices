package com.bank.transfer.TransferService;

import com.bank.transfer.dto.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExchangeClient {
    private final RestTemplate restTemplate;
    private final String exchangeServiceUrl = "http://localhost:8086";

    public ExchangeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public double convert(@Valid TransferRequest request) {
        ResponseEntity<Double> response = restTemplate.postForEntity(
                exchangeServiceUrl + "/convert",
                request,
                Double.class
        );
        return response.getBody();
    }
}
