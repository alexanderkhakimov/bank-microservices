package com.bank.accounts.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients")
@Setter
@Getter
public class ClientProperties {
    private ClientConfig notificationClient;

    @Setter
    @Getter
    public static class ClientConfig {
        private String baseurl;
    }
}
