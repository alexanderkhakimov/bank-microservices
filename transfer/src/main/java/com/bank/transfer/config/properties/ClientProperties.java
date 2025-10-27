package com.bank.transfer.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients")
@Setter
@Getter
public class ClientProperties {

    private ClientConfig userClient;
    private ClientConfig blockerClient;
    private ClientConfig notificationClient;
    private ClientConfig exchangeClient;

    @Setter
    @Getter
    public static class ClientConfig {
        private String baseurl;
    }
}
