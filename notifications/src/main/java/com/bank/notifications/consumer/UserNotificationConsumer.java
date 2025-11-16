package com.bank.notifications.consumer;

import com.bank.kafka.consumer.KafkaMessageConsumer;
import com.bank.kafka.event.UserNotificationRequested;
import com.bank.notifications.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationConsumer extends KafkaMessageConsumer<UserNotificationRequested> {

    private final NotificationsService service;

    @Override
    protected void process(UserNotificationRequested event) {
        service.sendNotifications(event.email());
    }

    @Override
    protected Class<UserNotificationRequested> getEventType() {
        return UserNotificationRequested.class;
    }
}
