package com.bank.cash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClient() {
        return RestClient.builder()
                .requestInterceptor(bearerTokenInterceptor());
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