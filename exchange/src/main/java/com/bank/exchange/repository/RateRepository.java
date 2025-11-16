package com.bank.exchange.repository;

import com.bank.exchange.model.Rate;
import com.bank.kafka.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate,Long> {
    Optional<Rate> findByCurrency(Currency currency);
}
