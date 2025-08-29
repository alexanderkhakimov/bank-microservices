package com.bank.frontui.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user_accounts")
@Builder
public record UserAccount(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,

        String keyCloakId,

        @NotBlank
        String login,

        @NotBlank
        String name,

        @Email
        String email,

        @NotNull
        LocalDate birthdate,

        @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
        List<AccountBalance> balances
) {
    public UserAccount {
        balances = balances != null ? List.copyOf(balances) : List.of();
    }
}
