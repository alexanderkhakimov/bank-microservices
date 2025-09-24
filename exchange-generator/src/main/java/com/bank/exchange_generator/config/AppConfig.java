package com.bank.exchange_generator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Value("${exchange.api.url:http://localhost:8086}")
    private String exchangeUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(exchangeUrl)
                .requestInterceptor(bearerTokenInterceptor()).build();
    }

    private ClientHttpRequestInterceptor bearerTokenInterceptor() {
        return (request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                String token = jwt.getTokenValue();
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }

            return execution.execute(request, body);
        };
    }
}
