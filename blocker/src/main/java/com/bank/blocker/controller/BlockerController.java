package com.bank.blocker.controller;

import com.bank.blocker.dto.BlockUserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.random.RandomGenerator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blocker")
@Slf4j
public class BlockerController {

    private final RandomGenerator randomGenerator;

    @PostMapping("")
    public ResponseEntity<String> blockUsers(@RequestBody BlockUserRequestDto requestDto) {
        if (!randomGenerator.nextBoolean()) {
            log.warn("Счет {} пользователя {} был заблокирован из за выполнения операции \"{}\"", requestDto.currency(), requestDto.login(), requestDto.operation());
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
        }
        return ResponseEntity.ok().build();
    }
}
