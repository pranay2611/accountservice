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

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccountDetails(@PathVariable String accountNumber) {
        Account account = accountService.getAccountDetails(accountNumber);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountNumber}/close")
    public ResponseEntity<Void> closeAccount(@PathVariable String accountNumber) {
        accountService.closeAccount(accountNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<Map<String, BigDecimal>> fetchBalance(@PathVariable String accountNumber) {
        BigDecimal balance = accountService.fetchBalance(accountNumber);
        Map<String, BigDecimal> response = Map.of("balance", balance);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountNumber}/balance")
    public ResponseEntity<Void> updateBalance(@PathVariable String accountNumber, @RequestParam BigDecimal newBalance) {
        accountService.updateBalance(accountNumber, newBalance);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountNumber}/status")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable String accountNumber) {
        AccountStatus status = accountService.getStatus(accountNumber);
        Map<String, String> response = Map.of("status", status.name());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable String accountNumber, @RequestParam String status) {
        accountService.changeStatus(accountNumber, AccountStatus.fromString(status));
        return ResponseEntity.ok().build();
    }
}
