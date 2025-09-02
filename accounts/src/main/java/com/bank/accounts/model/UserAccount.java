package com.bank.accounts.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyClockId;

    @NotBlank
    private String login;

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    String password;
    @NotNull
    private LocalDate birthdate;

    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AccountBalance> balances = new ArrayList<>();

}