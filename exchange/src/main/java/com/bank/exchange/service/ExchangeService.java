package com.bank.exchange.service;

import com.bank.exchange.dto.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class ExchangeService {
    public double convert(TransferRequest request) {
        return request.value() * 10;
    }
}
