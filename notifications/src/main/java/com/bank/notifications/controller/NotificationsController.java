package com.bank.notifications.controller;

import com.bank.notifications.dto.NotificationsUserRequestDto;
import com.bank.notifications.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@Slf4j
@RequiredArgsConstructor
public class NotificationsController {

    private final NotificationsService notificationsService;

    @PostMapping("/{login}")
    public ResponseEntity<String> notificateUsers(
            @RequestBody NotificationsUserRequestDto requestDto,
            @PathVariable String login
    ) {
        log.warn("Пришло оповещение от {} и сообщением {}", login, requestDto.message());
        notificationsService.sendNotifications(requestDto.email());
        return ResponseEntity.ok().build();
    }

}
