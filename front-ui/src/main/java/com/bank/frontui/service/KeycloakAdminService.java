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
    @Value("${keycloak.admin.realm}")
    private String realm;

    private final Keycloak keycloak;

    public KeycloakAdminService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public String registerUser(String login, String password, String name, String email, LocalDate dob) {

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
                        throw new RuntimeException("Ошибка при регистрации в Keycloak: " + response.getStatusInfo().getReasonPhrase());
                    }
                })
                .block();
    }

    public void updatePasswordKeycloak(String login, String password) {
        var users = keycloak.realm(realm).users().search(login, true);
        if (users.isEmpty()) {
            throw new RuntimeException("Пользователь %s не найден".formatted(login));
        }
        var userRepresentation = users.get(0);
        var userId = userRepresentation.getId();
        var credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        try {
            keycloak.realm(realm).users().get(userId).resetPassword(credential);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при регистрации в Keycloak: " + e.getMessage());
        }
    }
    public void updateAccount(String login, String name, LocalDate dob){
        var users = keycloak.realm(realm).users().search(login, true);
        if (users.isEmpty()) {
            throw new RuntimeException("Пользователь %s не найден".formatted(login));
        }
        var userRepresentation = users.getFirst();
        var userId = userRepresentation.getId();
        userRepresentation.setFirstName(name.split(" ")[0]);
        userRepresentation.setLastName(name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "");
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);
        userRepresentation.setAttributes(Map.of("birthdate", List.of(dob.toString())));
        try {
            keycloak.realm(realm).users().get(userId).update(userRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении в Keycloak: " + e.getMessage());
        }

    }
}
