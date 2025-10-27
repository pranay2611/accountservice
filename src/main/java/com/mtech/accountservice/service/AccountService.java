package com.mtech.accountservice.service;

import com.mtech.accountservice.entity.Account;
import com.mtech.accountservice.entity.AccountStatus;
import com.mtech.accountservice.entity.AccountType;
import com.mtech.accountservice.entity.Currency;
import com.mtech.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Long customerId, AccountType accountType, BigDecimal initialBalance, Currency currency) {
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(accountType);
        account.setBalance(initialBalance);
        account.setCurrency(currency);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCreatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    @Transactional
    public void closeAccount(Long id) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setStatus(AccountStatus.CLOSED);
            accountRepository.save(account);
        }
    }

    public Account getAccountDetails(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
    }

    public BigDecimal fetchBalance(Long id) {
        return accountRepository.findById(id)
                .map(Account::getBalance)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    @Transactional
    public void updateBalance(Long id, BigDecimal newBalance) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setBalance(newBalance);
            accountRepository.save(account);
        } else {
            throw new IllegalArgumentException("Account not found with ID: " + id);
        }
    }

    public AccountStatus getStatus(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
        return account.getStatus();
    }

    public void changeStatus(Long id, AccountStatus status) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setStatus(status);
            accountRepository.save(account);
        }
    }

    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        return String.valueOf(Math.abs(UUID.randomUUID().toString().hashCode())) + new Random().nextInt(10);
    }
}
