package com.bank.frontui.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients")
@Getter
@Setter
public class ClientProperties {
    private ClientConfig userClient;
    private ClientConfig transferClient;
    private ClientConfig cashClient;
    private ClientConfig exchangeClient;

    @Getter
    @Setter
    public static class ClientConfig {
        private String baseUrl;
    }
}
