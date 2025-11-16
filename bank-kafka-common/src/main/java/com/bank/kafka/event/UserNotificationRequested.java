package com.bank.kafka.event;

public record UserNotificationRequested(
        String email,
        String message,
        String source
) {
}
