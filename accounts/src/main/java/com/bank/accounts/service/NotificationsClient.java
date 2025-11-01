package com.bank.accounts.service;

import com.bank.accounts.config.properties.ClientProperties;
import com.bank.accounts.dto.NotificationsUserRequestDto;
import com.bank.accounts.exception.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class NotificationsClient {
    final private RestClient restClient;

    public NotificationsClient(RestClient.Builder restClient, ClientProperties clientProperties) {
        this.restClient = restClient.
                baseUrl(clientProperties.getNotificationClient().getBaseurl())
                .build();
    }

    public void sendNotifications(String login, String email, String message) {
        log.info("Сервис Account направляет оповещение пользователя {}", login);
        final var request = NotificationsUserRequestDto.builder()
                .email(email)
                .message(message).build();
        restClient.post()
                .uri("/{login}", login)
                .body(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (request1, response) -> {
                    throw new NotificationException("Клиетская ошибка: " + response.getStatusCode());
                })
                .onStatus(status -> status.is5xxServerError(), (request1, response) -> {
                    throw new NotificationException("Серверная ошибка: " + response.getStatusCode());
                })
                .toBodilessEntity();

    }
}
