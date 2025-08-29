package com.bank.accounts.repository;

import com.bank.accounts.model.AccountBalance;
import com.bank.accounts.model.Currency;
import com.bank.accounts.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {

    Optional<AccountBalance> findByUserAccountAndCurrency(UserAccount userAccount, Currency currency);

    List<AccountBalance> findAllByUserAccount(UserAccount account);
}
