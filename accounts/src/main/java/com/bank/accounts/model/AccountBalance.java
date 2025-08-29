package com.bank.accounts.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalance {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_account_id")
        private UserAccount userAccount;

        @Enumerated(EnumType.STRING)
        private Currency currency;

        private Double balance;
}