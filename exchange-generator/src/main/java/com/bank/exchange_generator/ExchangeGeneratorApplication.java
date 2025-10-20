package com.bank.exchange_generator;

import com.bank.exchange_generator.config.properties.ClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ClientProperties.class)
public class ExchangeGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeGeneratorApplication.class, args);
    }
}
