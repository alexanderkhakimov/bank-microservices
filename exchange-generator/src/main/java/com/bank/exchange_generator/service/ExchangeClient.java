package com.bank.exchange_generator.service;

import com.bank.exchange_generator.dto.UpdateRateRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Slf4j
@Service
public class ExchangeClient {
    @Value("${exchange.api.url:http://localhost:8086/}")
    private String exchangeUrl;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id:exchange-generator}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret:Uppsm320Wyfw2Vn3EDcghnwEP7Dh9TVN}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri:http://localhost:8180/realms/bank/protocol/openid-connect/token}")
    private String tokenUri;

    private final RestTemplate restClient;

    public ExchangeClient(RestTemplate restClient) {
        this.restClient = restClient;
    }

    private String getAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            log.debug("Requesting token from {} with client_id: {}, body: {}", tokenUri, clientId, body);

            ResponseEntity<TokenResponse> response = restClient.postForEntity(tokenUri, request, TokenResponse.class);
            log.debug("Token response status: {}, body: {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getAccessToken() != null) {
                log.info("Successfully obtained access token: {}", response.getBody().getAccessToken());
                return response.getBody().getAccessToken();
            } else {
                log.error("Failed to obtain access token: status {}, body {}", response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (HttpClientErrorException e) {
            log.error("HTTP error obtaining access token: status {}, response {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error obtaining access token: ", e);
            return null;
        }
    }

    public void updateRates(List<UpdateRateRequestDto> rates) {
        try {
            String token = getAccessToken();
            if (token == null) {
                log.error("No access token available, cannot send rates");
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            HttpEntity<List<UpdateRateRequestDto>> request = new HttpEntity<>(rates, headers);
            ResponseEntity<Void> response = restClient.exchange(exchangeUrl, HttpMethod.POST, request, Void.class);
            log.info("Successfully sent rates to {}, status: {}", exchangeUrl, response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("HTTP error sending rates to {}: status {}, response {}", exchangeUrl, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Failed to send rates: ", e);
        }
    }

    private static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public String toString() {
            return "TokenResponse{accessToken='" + (accessToken != null ? accessToken.substring(0, 10) + "..." : null) + "'}";
        }
    }
}