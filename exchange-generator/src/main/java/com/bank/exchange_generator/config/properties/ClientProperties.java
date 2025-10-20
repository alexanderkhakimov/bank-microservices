package com.bank.exchange_generator.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients")
@Getter
@Setter
public class ClientProperties {
    private ClientConfig exchangeClient;

    @Getter
    @Setter
    public static class ClientConfig {
        private String baseUrl;
    }
}
