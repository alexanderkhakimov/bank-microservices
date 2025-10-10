package com.bank.frontui.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {
    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .authorizeExchange(auth -> auth
                        .pathMatchers("/actuator/**","/signup", "/login","/logout-page").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/main"))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private ServerLogoutSuccessHandler logoutSuccessHandler() {
        OidcClientInitiatedServerLogoutSuccessHandler handler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri("{baseUrl}/login?logout=true");
        log.info("Запрос на выход сформирован");
        return ((exchange, authentication) -> {
            log.info("Начало процесса выхода. Authentication: {}", authentication);
            log.info("Base URL: {}", exchange.getExchange().getRequest().getURI().resolve("/"));

            return handler.onLogoutSuccess(exchange, authentication)
                    .doOnSuccess(v -> log.info("Успешный выход из Keycloak"))
                    .doOnError(e -> log.error("Ошибка при выходе: {}", e.getMessage()));
        });
    }


}
