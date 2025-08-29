package com.bank.frontui.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Entity
@Builder
@Table(name = "account_balances")
public record AccountBalance(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,

        @ManyToOne
        @JoinColumn(name = "user_account_id")
        UserAccount userAccount,

        @Enumerated(EnumType.STRING)
        Currency currency,
        @PositiveOrZero
        double balance
) {
}
