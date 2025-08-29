package com.bank.frontui.service;

import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.github.resilience4j.retry.Retry;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Data
public class KeycloakAdminService {
    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;


    public String registerUser(String login, String password, String name, String email, LocalDate dob) {
        var keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();

        var user = new UserRepresentation();
        user.setUsername(login);
        user.setFirstName(name.split(" ")[0]);
        user.setLastName(name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "");
        user.setEmail(email);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setAttributes(Map.of("birthdate", List.of(dob.toString())));

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        return Mono.fromCallable(() -> {
                    Response response = keycloak.realm(realm).users().create(user);
                    if (response.getStatus() == 201) {
                        String path = response.getLocation().getPath();
                        return path.substring(path.lastIndexOf("/") + 1);
                    } else {
                        throw new RuntimeException("Failed to register user: " + response.getStatusInfo().getReasonPhrase());
                    }
                })
                .block();
    }
}
