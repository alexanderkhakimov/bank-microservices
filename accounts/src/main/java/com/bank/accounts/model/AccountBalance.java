package com.bank.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "account_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    @JsonProperty("currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @JsonProperty("balance")
    private BigDecimal balance;

    @JsonProperty("isExists")
    private boolean isExists;
}