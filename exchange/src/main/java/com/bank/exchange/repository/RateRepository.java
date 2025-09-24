package com.bank.exchange.repository;

import com.bank.exchange.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRepository extends JpaRepository<Rate,Long> {
}
