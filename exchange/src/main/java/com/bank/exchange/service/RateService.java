package com.bank.exchange.service;

import com.bank.exchange.model.Rate;
import com.bank.exchange.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateService {
    private final RateRepository rateRepository;

    @Transactional
    public void updateAll(List<Rate> rate) {
        rateRepository.deleteAll();
        rateRepository.saveAll(rate);
    }

    public List<Rate> getRatesAll() {
        return rateRepository.findAll();
    }
}
