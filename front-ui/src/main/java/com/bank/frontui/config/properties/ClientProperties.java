package com.bank.frontui.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "clients")
@Getter
@Setter
public class ClientProperties {
    private ClientConfig userClient;
    private ClientConfig transferClient;
    private ClientConfig cashClient;

    @Getter
    @Setter
    public static class ClientConfig {
        private String baseUrl;
    }
}
