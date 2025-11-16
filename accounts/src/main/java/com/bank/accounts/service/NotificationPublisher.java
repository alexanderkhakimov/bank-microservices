package com.bank.accounts.service;

import com.bank.kafka.event.UserNotificationRequested;
import com.bank.kafka.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final KafkaMessageProducer<UserNotificationRequested> producer;

    public void sendNotification(String email, String message) {
        var event = new UserNotificationRequested(email, message, "exchange");

        producer.publish("notifications", email, event);
    }
}
