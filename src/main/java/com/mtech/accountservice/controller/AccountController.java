package com.mtech.accountservice.controller;

import com.mtech.accountservice.entity.Account;
import com.mtech.accountservice.entity.AccountStatus;
import com.mtech.accountservice.entity.AccountType;
import com.mtech.accountservice.entity.Currency;
import com.mtech.accountservice.service.AccountService;
import com.mtech.accountservice.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final CustomerService customerService;

    public AccountController(AccountService accountService, CustomerService customerService) {
        this.accountService = accountService;
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestParam Long customerId,
                                                 @RequestParam AccountType accountType,
                                                 @RequestParam BigDecimal initialBalance,
                                                 @RequestParam Currency currency) {

        if (!customerService.isCustomerPresent(customerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found with ID: " + customerId);
        }

        Account account = accountService.createAccount(customerId, accountType, initialBalance, currency);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountDetails(@PathVariable Long id) {
        Account account = accountService.getAccountDetails(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Void> closeAccount(@PathVariable Long id) {
        accountService.closeAccount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, BigDecimal>> fetchBalance(@PathVariable Long id) {
        BigDecimal balance = accountService.fetchBalance(id);
        Map<String, BigDecimal> response = Map.of("balance", balance);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<Void> updateBalance(@PathVariable Long id, @RequestParam BigDecimal newBalance) {
        accountService.updateBalance(id, newBalance);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable Long id) {
        AccountStatus status = accountService.getStatus(id);
        Map<String, String> response = Map.of("status", status.name());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam String status) {
        accountService.changeStatus(id, AccountStatus.fromString(status));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account updatedAccount) {
        Account existingAccount = accountService.getAccountDetails(id);

        if (updatedAccount.getAccountType() != null) {
            existingAccount.setAccountType(updatedAccount.getAccountType());
        }
        if (updatedAccount.getBalance() != null) {
            existingAccount.setBalance(updatedAccount.getBalance());
        }
        if (updatedAccount.getCurrency() != null) {
            existingAccount.setCurrency(updatedAccount.getCurrency());
        }
        if (updatedAccount.getStatus() != null) {
            existingAccount.setStatus(updatedAccount.getStatus());
        }

        Account savedAccount = accountService.saveAccount(existingAccount);
        return ResponseEntity.ok(savedAccount);
    }
}
