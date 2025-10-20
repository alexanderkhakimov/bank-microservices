package com.bank.exchange_generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final ReactiveOAuth2AuthorizedClientManager manager;

    public Mono<String> getTokenValue() {
        Authentication servicePrincipal = createServicePrincipal();

        return manager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("keycloak")
                        .principal(servicePrincipal)
                        .build()
                )
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(AbstractOAuth2Token::getTokenValue)
                .doOnSuccess(tokenValue -> log.info("Токен успешно получен"))
                .doOnError(error -> log.error("Ошибка извлечения токена: {}", error.getMessage()));
    }

    private Authentication createServicePrincipal() {
        return new UsernamePasswordAuthenticationToken(
                "api-gateway",
                null,
                null);
    }

}
