package com.bank.cash;

import com.bank.cash.config.properties.ClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ClientProperties.class)
@SpringBootApplication
public class CashApplication {
    public static void main(String[] args) {
        SpringApplication.run(CashApplication.class, args);
    }
}
