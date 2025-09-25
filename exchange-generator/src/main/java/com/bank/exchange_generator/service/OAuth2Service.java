package com.bank.exchange_generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final OAuth2AuthorizedClientManager manager;

    public String getTokenValue() {
        try {
            return manager.authorize(OAuth2AuthorizeRequest
                            .withClientRegistrationId("keycloak")
                            .principal("system")
                            .build()
                    )
                    .getAccessToken()
                    .getTokenValue();
        } catch (Exception e) {
            log.error("Не удалалось получить токен: ", e);
            return null;
        }
    }
}
