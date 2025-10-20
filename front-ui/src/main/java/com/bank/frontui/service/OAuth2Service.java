package com.bank.frontui.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2Service {
    private final ReactiveOAuth2AuthorizedClientManager manager;

    public Mono<String> getTokenValue() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .switchIfEmpty(Mono.error(new RuntimeException("Пользователь не аутентифицирован")))
                .flatMap(authentication ->
                    manager.authorize(OAuth2AuthorizeRequest
                                    .withClientRegistrationId("keycloak")
                                    .principal(authentication)
                                    .build()
                            )
                            .map(OAuth2AuthorizedClient::getAccessToken)
                            .map(AbstractOAuth2Token::getTokenValue)
                )
                .doOnSuccess(tokenValue -> log.info("Токен успешно получен"))
                .doOnError(error -> log.error("Ошибка извлечения токена: {}", error.getMessage()));
    }
}
