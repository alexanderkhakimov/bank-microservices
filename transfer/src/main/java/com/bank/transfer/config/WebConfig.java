package com.bank.transfer.config;

import com.bank.transfer.LoggingFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoggingFilter loggingFilter;

    public WebConfig(LoggingFilter loggingFilter) {
        this.loggingFilter = loggingFilter;
    }
}