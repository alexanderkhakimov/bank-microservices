package com.bank.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationsService {

    public void sendNotifications(String email) {
        log.warn("Оповещение отправлено на почту {}", email);
    }
}
