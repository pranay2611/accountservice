package com.mtech.accountservice.controller;

import com.mtech.accountservice.entity.Account;
import com.mtech.accountservice.entity.AccountStatus;
import com.mtech.accountservice.entity.AccountType;
import com.mtech.accountservice.entity.Currency;
import com.mtech.accountservice.service.AccountService;
import com.mtech.accountservice.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;
    private final CustomerService customerService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.account.creation.queue}")
    private String accountCreationQueue;

    @Value("${rabbitmq.account.update.queue}")
    private String accountUpdateQueue;

    public AccountController(AccountService accountService, CustomerService customerService, RabbitTemplate rabbitTemplate) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account accountPayload) {

        if (!customerService.isCustomerPresent(accountPayload.getCustomerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found with ID: " + accountPayload.getCustomerId());
        }

        Account account = accountService.createAccount(
                accountPayload.getCustomerId(),
                accountPayload.getAccountType(),
                accountPayload.getBalance(),
                accountPayload.getCurrency()
        );

        // Publish account creation event
        rabbitTemplate.convertAndSend(accountCreationQueue, account);
        logger.info("Publishing event to RabbitMQ. Exchange: {}, RoutingKey: {}, Payload: {}", "", accountCreationQueue, account);

        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountDetails(@PathVariable Long id) {
        Account account = accountService.getAccountDetails(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Void> closeAccount(@PathVariable Long id) {
        Account account = accountService.closeAccount(id);

        // Publish account update event
        rabbitTemplate.convertAndSend(accountUpdateQueue, account);
        logger.info("Publishing event to RabbitMQ. Exchange: {}, RoutingKey: {}, Payload: {}", "", accountUpdateQueue, account);
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
        Account account = accountService.updateBalance(id, newBalance);

        // Publish account update event
        rabbitTemplate.convertAndSend(accountUpdateQueue, account);
        logger.info("Publishing event to RabbitMQ. Exchange: {}, RoutingKey: {}, Payload: {}", "", accountUpdateQueue, account);
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
        Account account = accountService.changeStatus(id, AccountStatus.fromString(status));

        // Publish account update event
        rabbitTemplate.convertAndSend(accountUpdateQueue, account);
        logger.info("Publishing event to RabbitMQ. Exchange: {}, RoutingKey: {}, Payload: {}", "", accountUpdateQueue, account);

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

        // Publish account update event
        rabbitTemplate.convertAndSend(accountUpdateQueue, savedAccount);
        logger.info("Publishing event to RabbitMQ. Exchange: {}, RoutingKey: {}, Payload: {}", "", accountUpdateQueue, savedAccount);

        return ResponseEntity.ok(savedAccount);
    }
}
