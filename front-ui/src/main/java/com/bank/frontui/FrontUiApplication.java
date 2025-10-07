package com.bank.frontui;

import com.bank.frontui.config.properties.ClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ClientProperties.class)
public class FrontUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrontUiApplication.class, args);
	}

}
