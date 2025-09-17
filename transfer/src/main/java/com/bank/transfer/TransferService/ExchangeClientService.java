package com.bank.transfer.TransferService;

import com.bank.transfer.controller.TransferController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeClientService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ExchangeClientService.class);
    private final String accountServiceUrl = "http://localhost:8086/getInfo";

    public ExchangeClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getInfoFromExchange() {
        logger.info("Запрос из трасфера в обмен направлен!");
        return restTemplate.getForObject(accountServiceUrl, String.class);
    }
}
