package com.bank.blocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.random.RandomGenerator;

@SpringBootApplication
public class BlockerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlockerApplication.class, args);
    }
    @Bean
    public RandomGenerator randomGenerator(){
        return RandomGenerator.getDefault();
    }
}
