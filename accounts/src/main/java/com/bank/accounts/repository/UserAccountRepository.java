package com.bank.accounts.repository;

import com.bank.accounts.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByKeyClockId(String keyClockId);

    Optional<UserAccount> findByLogin(String login);
}
